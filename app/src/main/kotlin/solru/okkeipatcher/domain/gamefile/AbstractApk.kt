package solru.okkeipatcher.domain.gamefile

import com.android.apksig.ApkSigner
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.installPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.operation.AbstractOperation
import solru.okkeipatcher.domain.util.deleteTempZipFiles
import solru.okkeipatcher.domain.util.extension.use
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.service.StreamCopier
import solru.okkeipatcher.util.Preferences
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec

private const val CERTIFICATE_FILE_NAME = "testkey.x509.pem"
private const val PRIVATE_KEY_FILE_NAME = "testkey.pk8"

abstract class AbstractApk(
	protected val commonFiles: CommonFiles,
	protected val streamCopier: StreamCopier,
	protected val ioDispatcher: CoroutineDispatcher
) : Apk {

	override val backupExists: Boolean
		get() = commonFiles.backupApk.exists

	private val tempZipFiles = mutableListOf<ZipFile>()
	private val tempZipFilesMutex = Mutex()

	override fun canPatch(onNegative: (LocalizedString) -> Unit): Boolean {
		val canInstallPatchedApk = backupExists && commonFiles.signedApk.exists
		if (!Apk.isInstalled && canInstallPatchedApk) {
			return true
		}
		return if (Apk.isInstalled) {
			true
		} else {
			onNegative(LocalizedString.resource(R.string.error_game_not_found))
			false
		}
	}

	override fun deleteBackup() = commonFiles.backupApk.delete()

	override fun backup() = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		override suspend fun invoke() {
			_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (commonFiles.backupApk.verify().invoke()) {
				_progressDelta.emit(progressMax)
				return
			}
			if (!Apk.isInstalled) {
				throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
			}
			try {
				_status.emit(LocalizedString.resource(R.string.status_backing_up_apk))
				val hash = copyInstalledApkTo(commonFiles.backupApk, hashing = true)
				_status.emit(LocalizedString.resource(R.string.status_writing_apk_hash))
				Preferences.set(CommonFileHashKey.backup_apk_hash.name, hash)
				_progressDelta.emit(progressMax)
			} catch (t: Throwable) {
				commonFiles.backupApk.delete()
				throw t
			}
		}
	}

	override fun restore() = object : AbstractOperation<Unit>() {

		private val uninstallOperation = uninstall(updating = false)
		private val installBackupOperation = install(File(commonFiles.backupApk.fullPath))

		override val status = merge(
			uninstallOperation.status,
			installBackupOperation.status,
			_status
		)

		override val progressDelta = merge(
			uninstallOperation.progressDelta,
			installBackupOperation.progressDelta,
			_progressDelta
		)

		override val progressMax = uninstallOperation.progressMax + installBackupOperation.progressMax

		override suspend fun invoke() {
			_progressDelta.emit(0)
			if (!commonFiles.backupApk.exists) {
				throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
			}
			_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (!commonFiles.backupApk.verify().invoke()) {
				throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
			}
			uninstallOperation()
			installBackupOperation()
		}
	}

	/**
	 * Closes all [ZipFile] instances gotten from [asZipFile] and deletes temporary files.
	 */
	override fun close() {
		deleteTempZipFiles(OkkeiStorage.external)
		commonFiles.tempApk.delete()
		runBlocking {
			tempZipFilesMutex.withLock {
				tempZipFiles.forEach {
					it.executorService?.shutdownNow()
					it.close()
				}
				tempZipFiles.clear()
			}
		}
	}

	/**
	 * Creates temporary copy of game APK if it doesn't exist.
	 *
	 * @return temp copy of game APK represented as [ZipFile].
	 */
	suspend fun asZipFile(): ZipFile {
		if (!commonFiles.tempApk.exists) {
			copyInstalledApkTo(commonFiles.tempApk)
		}
		return ZipFile(commonFiles.tempApk.fullPath)
			.also { zipFile ->
				tempZipFilesMutex.withLock {
					tempZipFiles.add(zipFile)
				}
			}
	}

	fun sign() = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		@Suppress("BlockingMethodInNonBlockingContext")
		override suspend fun invoke() {
			_status.emit(LocalizedString.resource(R.string.status_signing_apk))
			val certificate = getSigningCertificate()
			val privateKey = getSigningPrivateKey()
			val signerConfig = ApkSigner.SignerConfig.Builder(
				"OKKEI",
				privateKey,
				listOf(certificate)
			).build()
			val inputApk = File(commonFiles.tempApk.fullPath)
			val outputApk = File(commonFiles.signedApk.fullPath)
			val apkSigner = ApkSigner.Builder(listOf(signerConfig))
				.setCreatedBy("Okkei Patcher")
				.setInputApk(inputApk)
				.setOutputApk(outputApk)
				.build()
			withContext(ioDispatcher) {
				apkSigner.sign()
			}
			_status.emit(LocalizedString.resource(R.string.status_writing_patched_apk_hash))
			Preferences.set(
				CommonFileHashKey.signed_apk_hash.name,
				commonFiles.signedApk.computeHash().invoke()
			)
			_progressDelta.emit(progressMax)
		}
	}

	fun removeSignature() = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		override suspend fun invoke() {
			asZipFile().use { zipFile ->
				_status.emit(LocalizedString.resource(R.string.status_removing_signature))
				withContext(ioDispatcher) {
					zipFile.removeFile("META-INF/")
				}
			}
			_progressDelta.emit(progressMax)
		}
	}

	protected fun installPatched(updating: Boolean) = object : AbstractOperation<Unit>() {

		private val uninstallOperation = uninstall(updating)
		private val installOperation = install(File(commonFiles.signedApk.fullPath))

		override val status = merge(
			uninstallOperation.status,
			installOperation.status,
			_status
		)

		override val progressDelta = merge(
			uninstallOperation.progressDelta,
			installOperation.progressDelta,
			_progressDelta
		)

		override val progressMax = uninstallOperation.progressMax + installOperation.progressMax

		override suspend fun invoke() {
			if (!commonFiles.signedApk.exists) {
				throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
			}
			_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (!commonFiles.signedApk.verify().invoke()) {
				commonFiles.signedApk.delete()
				throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
			}
			uninstallOperation()
			installOperation()
			commonFiles.signedApk.delete()
		}
	}

	private fun uninstall(updating: Boolean) = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		override suspend fun invoke() {
			_status.emit(LocalizedString.resource(R.string.status_uninstalling))
			if (updating || !Apk.isInstalled) {
				_progressDelta.emit(progressMax)
				return
			}
			val uninstallResult = PackageUninstaller.uninstallPackage(Apk.PACKAGE_NAME)
			if (!uninstallResult) {
				throw LocalizedException(LocalizedString.resource(R.string.error_uninstall))
			}
			_progressDelta.emit(progressMax)
		}
	}

	private fun install(apkFile: File) = object : AbstractOperation<Unit>() {

		override val progressMax = 100

		override suspend fun invoke() {
			_status.emit(LocalizedString.resource(R.string.status_installing))
			val installResult = PackageInstaller.installPackage(apkFile)
			if (installResult is InstallResult.Failure) {
				throw LocalizedException(
					LocalizedString.resource(
						R.string.error_install,
						installResult.cause?.message.toString()
					)
				)
			}
			_progressDelta.emit(progressMax)
		}
	}

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	private suspend fun copyInstalledApkTo(
		destinationFile: solru.okkeipatcher.io.file.File,
		hashing: Boolean = false
	) = coroutineScope {
		if (!Apk.isInstalled) {
			throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
		}
		val installedApkPath = OkkeiApplication.context.packageManager
			.getPackageInfo(Apk.PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
		val installedApk = JavaFile(File(installedApkPath), streamCopier)
		val hash = installedApk.copyTo(destinationFile, hashing).invoke()
		hash
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun getSigningCertificate() = withContext(ioDispatcher) {
		val assets = OkkeiApplication.context.assets
		assets.open(CERTIFICATE_FILE_NAME).use { certificateStream ->
			val certificateFactory = CertificateFactory.getInstance("X.509")
			certificateFactory.generateCertificate(certificateStream) as X509Certificate
		}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun getSigningPrivateKey() = withContext(ioDispatcher) {
		val assets = OkkeiApplication.context.assets
		assets.openFd(PRIVATE_KEY_FILE_NAME).use { keyFd ->
			val keyByteArray = ByteArray(keyFd.declaredLength.toInt())
			keyFd.createInputStream().use {
				it.read(keyByteArray)
			}
			val keySpec = PKCS8EncodedKeySpec(keyByteArray)
			val keyFactory = KeyFactory.getInstance("RSA")
			keyFactory.generatePrivate(keySpec)
		}
	}
}