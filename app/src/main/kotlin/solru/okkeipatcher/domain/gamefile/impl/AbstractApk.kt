package solru.okkeipatcher.domain.gamefile.impl

import com.android.apksig.ApkSigner
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.installPackage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.lingala.zip4j.ZipFile
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.base.ObservableImpl
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.Apk
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.ProgressData
import solru.okkeipatcher.domain.util.deleteTempZipFiles
import solru.okkeipatcher.domain.util.extension.makeIndeterminate
import solru.okkeipatcher.domain.util.extension.observe
import solru.okkeipatcher.domain.util.extension.reset
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
	@IoDispatcher protected val ioDispatcher: CoroutineDispatcher
) : ObservableImpl(), Apk {

	override val backupExists: Boolean
		get() = commonFiles.backupApk.exists

	override val progress = merge(
		commonFiles.backupApk.progress,
		commonFiles.tempApk.progress,
		commonFiles.signedApk.progress,
		PackageInstaller.progress.map { ProgressData(it.progress, it.max, it.isIndeterminate) },
		progressPublisher._progress
	)

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

	override suspend fun backup() {
		progressPublisher._progress.reset()
		_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (verifyBackupIntegrity()) return
		try {
			if (!Apk.isInstalled) {
				throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
			}
			_status.emit(LocalizedString.resource(R.string.status_backing_up_apk))
			val hash = copyInstalledApkTo(commonFiles.backupApk, hashing = true)
			_status.emit(LocalizedString.resource(R.string.status_writing_apk_hash))
			Preferences.set(CommonFileHashKey.backup_apk_hash.name, hash)
		} catch (t: Throwable) {
			commonFiles.backupApk.delete()
			throw t
		}
	}

	override suspend fun restore() {
		progressPublisher._progress.reset()
		if (!commonFiles.backupApk.exists) {
			throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
		}
		_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFiles.backupApk.verify()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
		}
		_status.emit(LocalizedString.empty())
		if (Apk.isInstalled) {
			uninstall()
		}
		installBackup()
	}

	override suspend fun verifyBackupIntegrity() = commonFiles.backupApk.verify()

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
	 * @return temp copy of game APK represented as [ZipFile]. Has `isRunInThread` set to `true`.
	 */
	suspend fun asZipFile(): ZipFile {
		if (!commonFiles.tempApk.exists) {
			copyInstalledApkTo(commonFiles.tempApk)
		}
		return ZipFile(commonFiles.tempApk.fullPath)
			.apply { isRunInThread = true }
			.also { zipFile ->
				tempZipFilesMutex.withLock {
					tempZipFiles.add(zipFile)
				}
			}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	suspend fun sign() {
		_status.emit(LocalizedString.resource(R.string.status_signing_apk))
		progressPublisher._progress.makeIndeterminate()
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
			commonFiles.signedApk.computeHash()
		)
	}

	suspend fun removeSignature() {
		asZipFile().use { zipFile ->
			_status.emit(LocalizedString.resource(R.string.status_removing_signature))
			zipFile.removeFile("META-INF/")
			zipFile.progressMonitor.observe { progressData ->
				progressPublisher._progress.emit(progressData)
			}
		}
	}

	protected suspend fun installPatched(updating: Boolean) {
		if (!commonFiles.signedApk.exists) {
			throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
		}
		_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFiles.signedApk.verify()) {
			commonFiles.signedApk.delete()
			throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
		}
		if (!updating && Apk.isInstalled) {
			uninstall()
		}
		progressPublisher._progress.makeIndeterminate()
		_status.emit(LocalizedString.resource(R.string.status_installing))
		val installResult = PackageInstaller.installPackage(File(commonFiles.signedApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throw LocalizedException(
				LocalizedString.resource(
					R.string.error_install,
					installResult.cause?.message.toString()
				)
			)
		}
		commonFiles.signedApk.delete()
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
		_status.emit(LocalizedString.resource(R.string.status_copying_apk))
		val installedApkPath = OkkeiApplication.context.packageManager
			.getPackageInfo(Apk.PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
		val installedApk = JavaFile(File(installedApkPath), streamCopier)
		val progressJob = launch {
			progressPublisher._progress.emitAll(installedApk.progress)
		}
		val hash = installedApk.copyTo(destinationFile, hashing)
		progressJob.cancel()
		hash
	}

	private suspend inline fun uninstall() {
		_status.emit(LocalizedString.resource(R.string.status_uninstalling))
		progressPublisher._progress.makeIndeterminate()
		val uninstallResult = PackageUninstaller.uninstallPackage(Apk.PACKAGE_NAME)
		if (!uninstallResult) {
			throw LocalizedException(LocalizedString.resource(R.string.error_uninstall))
		}
	}

	private suspend inline fun installBackup() {
		_status.emit(LocalizedString.resource(R.string.status_installing))
		val installResult = PackageInstaller.installPackage(File(commonFiles.backupApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throw LocalizedException(
				LocalizedString.resource(
					R.string.error_install,
					installResult.cause?.message.toString()
				)
			)
		}
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