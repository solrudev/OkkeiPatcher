package solru.okkeipatcher.core.services.files.impl

import android.content.res.AssetManager
import com.aefyr.pseudoapksigner.PseudoApkSigner
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
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.services.ObservableServiceImpl
import solru.okkeipatcher.core.services.files.PatchableGameFile
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFiles
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.getPackagePublicSourceDir
import solru.okkeipatcher.utils.isPackageInstalled
import java.io.File
import java.io.FileOutputStream

abstract class BaseApk(
	protected val commonFiles: CommonFiles,
	protected val streamCopier: StreamCopier,
	protected val ioDispatcher: CoroutineDispatcher
) : ObservableServiceImpl(), PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFiles.backupApk.exists

	private val privateKeyFile = File(OkkeiStorage.private, PRIVATE_KEY_FILE_NAME)
	private val rsaTemplateFile = File(OkkeiStorage.private, RSA_TEMPLATE_FILE_NAME)
	private var isUpdating = false

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFiles.backupApk.progress,
		commonFiles.tempApk.progress,
		commonFiles.signedApk.progress,
		PackageInstaller.progress.map { ProgressData(it.progress, it.max, it.isIndeterminate) },
		progressPublisher.mutableProgress
	)

	override suspend fun update(manifest: OkkeiManifest) {
		try {
			isUpdating = true
			progressPublisher.mutableProgress.reset()
			commonFiles.tempApk.delete()
			commonFiles.signedApk.delete()
			patch(manifest)
		} finally {
			isUpdating = false
		}
	}

	override fun deleteBackup() = commonFiles.backupApk.delete()

	override suspend fun backup() {
		try {
			progressPublisher.mutableProgress.reset()
			if (!isPackageInstalled(PACKAGE_NAME)) {
				throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (verifyBackupIntegrity()) return
			mutableStatus.emit(LocalizedString.resource(R.string.status_backing_up_apk))
			val hash = copyOriginalApkTo(commonFiles.backupApk, hashing = true)
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
		if (isPackageInstalled(PACKAGE_NAME)) {
			uninstall()
		}
		installBackup()
	}

	override suspend fun verifyBackupIntegrity() = commonFiles.backupApk.verify()

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	protected suspend fun copyOriginalApkTo(
		destinationFile: solru.okkeipatcher.io.file.File,
		hashing: Boolean = false
	) = coroutineScope {
		mutableStatus.emit(LocalizedString.resource(R.string.status_copying_apk))
		val originalApk = JavaFile(File(getPackagePublicSourceDir(PACKAGE_NAME)), streamCopier)
		val progressJob = launch {
			progressPublisher.mutableProgress.emitAll(originalApk.progress)
		}
		val hash = originalApk.copyTo(destinationFile, hashing)
		progressJob.cancel()
		hash
	}

	protected suspend fun installPatched() {
		if (!commonFiles.signedApk.exists) {
			throw OkkeiException(LocalizedString.resource(R.string.error_apk_not_found_patch))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFiles.signedApk.verify()) {
			commonFiles.signedApk.delete()
			throw OkkeiException(LocalizedString.resource(R.string.error_not_trustworthy_apk_patch))
		}
		if (!isUpdating) {
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
	protected suspend fun removeSignature(apkZip: ZipFile) {
		mutableStatus.emit(LocalizedString.resource(R.string.status_removing_signature))
		withContext(ioDispatcher) {
			val apkProgressMonitor = apkZip.progressMonitor
			apkZip.removeFile("META-INF/")
			while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
				ensureActive()
				progressPublisher.mutableProgress.emit(
					apkProgressMonitor.workCompleted.toInt(),
					apkProgressMonitor.totalWork.toInt()
				)
				delay(30)
			}
		}
	}

	private suspend inline fun extractSigningKey() {
		val assets = OkkeiApplication.context.assets
		if (!privateKeyFile.exists()) {
			assets.copyAssetToFile(PRIVATE_KEY_FILE_NAME, privateKeyFile)
		}
		if (!rsaTemplateFile.exists()) {
			assets.copyAssetToFile(RSA_TEMPLATE_FILE_NAME, rsaTemplateFile)
		}
	}

	protected suspend fun sign() {
		mutableStatus.emit(LocalizedString.resource(R.string.status_signing_apk))
		extractSigningKey()
		progressPublisher.mutableProgress.makeIndeterminate()
		commonFiles.signedApk.delete()
		commonFiles.signedApk.create()
		commonFiles.tempApk.createInputStream().use { tempApk ->
			commonFiles.signedApk.createOutputStream().use { signedApk ->
				PseudoApkSigner(rsaTemplateFile, privateKeyFile).sign(tempApk, signedApk)
			}
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_writing_patched_apk_hash))
		Preferences.set(
			CommonFileHashKey.signed_apk_hash.name,
			commonFiles.signedApk.computeHash()
		)
		commonFiles.tempApk.delete()
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun AssetManager.copyAssetToFile(assetName: String, file: File) = withContext(ioDispatcher) {
		openFd(assetName).use { fileDescriptor ->
			fileDescriptor.createInputStream().use { inputStream ->
				FileOutputStream(file).use { outputStream ->
					streamCopier.copy(
						inputStream,
						outputStream,
						fileDescriptor.length
					) { progressData -> progressPublisher.mutableProgress.emit(progressData) }
				}
			}
		}
	}

	companion object {
		const val PACKAGE_NAME = "com.mages.chaoschild_jp"
		protected const val PRIVATE_KEY_FILE_NAME = "testkey.pk8"
		protected const val RSA_TEMPLATE_FILE_NAME = "testkey.past"
	}
}