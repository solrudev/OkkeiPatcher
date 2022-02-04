package solru.okkeipatcher.domain.services.gamefile.impl

import com.android.apksig.ApkSigner
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.domain.model.files.common.CommonFileHashKey
import solru.okkeipatcher.domain.model.files.common.CommonFiles
import solru.okkeipatcher.domain.services.ObservableServiceImpl
import solru.okkeipatcher.domain.services.gamefile.PatchableGameFile
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.getPackagePublicSourceDir
import solru.okkeipatcher.utils.isPackageInstalled
import solru.okkeipatcher.utils.use
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec

private const val CERTIFICATE_FILE_NAME = "testkey.x509.pem"
private const val PRIVATE_KEY_FILE_NAME = "testkey.pk8"

abstract class Apk(
	protected val commonFiles: CommonFiles,
	protected val streamCopier: StreamCopier,
	@IoDispatcher protected val ioDispatcher: CoroutineDispatcher
) : ObservableServiceImpl(), PatchableGameFile {

	override val backupExists: Boolean get() = commonFiles.backupApk.exists

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFiles.backupApk.progress,
		commonFiles.tempApk.progress,
		commonFiles.signedApk.progress,
		PackageInstaller.progress.map { ProgressData(it.progress, it.max, it.isIndeterminate) },
		progressPublisher.mutableProgress
	)

	override fun deleteBackup() = commonFiles.backupApk.delete()

	final override suspend fun patch() {
		try {
			applyPatch()
		} catch (e: Throwable) {
			throw e
		} finally {
			commonFiles.tempApk.delete()
		}
	}

	final override suspend fun update() {
		try {
			applyUpdate()
		} catch (e: Throwable) {
			throw e
		} finally {
			commonFiles.tempApk.delete()
		}
	}

	override suspend fun backup() {
		try {
			progressPublisher.mutableProgress.reset()
			if (!isInstalled) {
				throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (verifyBackupIntegrity()) return
			mutableStatus.emit(LocalizedString.resource(R.string.status_backing_up_apk))
			val hash = copyInstalledApkTo(commonFiles.backupApk, hashing = true)
			mutableStatus.emit(LocalizedString.resource(R.string.status_writing_apk_hash))
			Preferences.set(CommonFileHashKey.backup_apk_hash.name, hash)
		} catch (e: Throwable) {
			commonFiles.backupApk.delete()
		}
	}

	override suspend fun restore() {
		progressPublisher.mutableProgress.reset()
		if (!commonFiles.backupApk.exists) {
			throw OkkeiException(LocalizedString.resource(R.string.error_apk_not_found_restore))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFiles.backupApk.verify()) {
			throw OkkeiException(LocalizedString.resource(R.string.error_not_trustworthy_apk_restore))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.empty))
		if (isInstalled) {
			uninstall()
		}
		installBackup()
	}

	override suspend fun verifyBackupIntegrity() = commonFiles.backupApk.verify()

	/**
	 * Creates temp copy of game APK if it doesn't exist.
	 *
	 * @return temp copy of game APK represented as [ZipFile].
	 */
	suspend fun asZipFile(): ZipFile {
		if (!commonFiles.tempApk.exists) {
			copyInstalledApkTo(commonFiles.tempApk)
		}
		return ZipFile(commonFiles.tempApk.fullPath).apply { isRunInThread = true }
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	suspend fun sign() {
		mutableStatus.emit(LocalizedString.resource(R.string.status_signing_apk))
		progressPublisher.mutableProgress.makeIndeterminate()
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
		mutableStatus.emit(LocalizedString.resource(R.string.status_writing_patched_apk_hash))
		Preferences.set(
			CommonFileHashKey.signed_apk_hash.name,
			commonFiles.signedApk.computeHash()
		)
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	suspend fun removeSignature() {
		mutableStatus.emit(LocalizedString.resource(R.string.status_removing_signature))
		withContext(ioDispatcher) {
			ZipFile(commonFiles.tempApk.fullPath).apply { isRunInThread = true }.use { zipFile ->
				val progressMonitor = zipFile.progressMonitor
				zipFile.removeFile("META-INF/")
				while (progressMonitor.state == ProgressMonitor.State.BUSY) {
					ensureActive()
					progressPublisher.mutableProgress.emit(
						progressMonitor.workCompleted.toInt(),
						progressMonitor.totalWork.toInt()
					)
					delay(20)
				}
			}
		}
	}

	protected abstract suspend fun applyPatch()
	protected abstract suspend fun applyUpdate()

	protected suspend fun installPatched(updating: Boolean) {
		if (!commonFiles.signedApk.exists) {
			throw OkkeiException(LocalizedString.resource(R.string.error_apk_not_found_patch))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFiles.signedApk.verify()) {
			commonFiles.signedApk.delete()
			throw OkkeiException(LocalizedString.resource(R.string.error_not_trustworthy_apk_patch))
		}
		if (!updating) {
			uninstall()
		}
		progressPublisher.mutableProgress.makeIndeterminate()
		mutableStatus.emit(LocalizedString.resource(R.string.status_installing))
		val installResult = PackageInstaller.installPackage(File(commonFiles.signedApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throw OkkeiException(LocalizedString.resource(R.string.error_install))
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
		if (!isInstalled) {
			throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_copying_apk))
		val originalApk = JavaFile(File(getPackagePublicSourceDir(PACKAGE_NAME)), streamCopier)
		val progressJob = launch {
			progressPublisher.mutableProgress.emitAll(originalApk.progress)
		}
		val hash = originalApk.copyTo(destinationFile, hashing)
		progressJob.cancel()
		hash
	}

	private suspend inline fun uninstall() {
		mutableStatus.emit(LocalizedString.resource(R.string.status_uninstalling))
		progressPublisher.mutableProgress.makeIndeterminate()
		val uninstallResult = PackageUninstaller.uninstallPackage(PACKAGE_NAME)
		if (!uninstallResult) {
			throw OkkeiException(LocalizedString.resource(R.string.error_uninstall))
		}
	}

	private suspend inline fun installBackup() {
		mutableStatus.emit(LocalizedString.resource(R.string.status_installing))
		val installResult = PackageInstaller.installPackage(File(commonFiles.backupApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throw OkkeiException(LocalizedString.resource(R.string.error_install))
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

	companion object {
		const val PACKAGE_NAME = "com.mages.chaoschild_jp"
		val isInstalled get() = isPackageInstalled(PACKAGE_NAME)
	}
}