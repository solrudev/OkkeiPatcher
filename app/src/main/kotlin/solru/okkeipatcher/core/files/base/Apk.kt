package solru.okkeipatcher.core.files.base

import android.content.res.AssetManager
import android.net.Uri
import com.aefyr.pseudoapksigner.PseudoApkSigner
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.base.FileWrapper
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFileInstances
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.getPackagePublicSourceDir
import solru.okkeipatcher.utils.isPackageInstalled
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import java.io.File
import java.io.FileOutputStream

abstract class Apk(
	protected val commonFileInstances: CommonFileInstances,
	protected val ioService: IoService,
	protected val ioDispatcher: CoroutineDispatcher
) : AppServiceBase(), PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFileInstances.backupApk.exists

	protected var isUpdating = false
		private set

	protected val originalApk by lazy {
		JavaFile(File(getPackagePublicSourceDir(PACKAGE_NAME)), ioService)
	}

	protected val privateKeyFile = File(OkkeiStorage.private, "testkey.pk8")
	protected val rsaTemplateFile = File(OkkeiStorage.private, "testkey.past")

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFileInstances.backupApk.progress,
		commonFileInstances.tempApk.progress,
		commonFileInstances.signedApk.progress,
		PackageInstaller.progress.map { ProgressData(it.progress, it.max, it.isIndeterminate) },
		progressMutable
	)

	override suspend fun update(manifest: OkkeiManifest) {
		try {
			isUpdating = true
			progressMutable.reset()
			commonFileInstances.tempApk.deleteIfExists()
			commonFileInstances.signedApk.deleteIfExists()
			patch(manifest)
		} finally {
			isUpdating = false
		}
	}

	override fun deleteBackup() {
		commonFileInstances.backupApk.deleteIfExists()
	}

	override suspend fun backup() =
		tryWrapper(onCatch = { commonFileInstances.backupApk.deleteIfExists() }) {
			progressMutable.reset()
			if (!isPackageInstalled(PACKAGE_NAME)) {
				throwErrorMessage(R.string.error_game_not_found)
			}
			statusMutable.emit(R.string.status_comparing_apk)
			if (verifyBackupIntegrity()) return
			statusMutable.emit(R.string.status_backing_up_apk)
			copyOriginalApkTo(commonFileInstances.backupApk)
			statusMutable.emit(R.string.status_writing_apk_hash)
			Preferences.set(
				CommonFileHashKey.backup_apk_hash.name,
				commonFileInstances.backupApk.computeMd5()
			)
		}

	override suspend fun restore() = tryWrapper {
		progressMutable.reset()
		if (!commonFileInstances.backupApk.exists) {
			throwErrorMessage(R.string.error_apk_not_found_restore)
		}
		statusMutable.emit(R.string.status_comparing_apk)
		if (!commonFileInstances.backupApk.verify()) {
			throwErrorMessage(R.string.error_not_trustworthy_apk_restore)
		}
		statusMutable.emit(R.string.empty)
		if (isPackageInstalled(PACKAGE_NAME)) {
			uninstall()
		}
		installBackup()
	}

	override suspend fun verifyBackupIntegrity() = commonFileInstances.backupApk.verify()

	protected suspend inline fun copyOriginalApkTo(destinationFile: FileWrapper) = coroutineScope {
		statusMutable.emit(R.string.status_copying_apk)
		val progressJob = launch {
			originalApk.progress.collect { progressMutable.emit(it) }
		}
		originalApk.copyTo(destinationFile)
		progressJob.cancel()
	}

	protected suspend inline fun installPatched() {
		if (!commonFileInstances.signedApk.exists) {
			throwErrorMessage(R.string.error_apk_not_found_patch)
		}
		statusMutable.emit(R.string.status_comparing_apk)
		if (!commonFileInstances.signedApk.verify()) {
			commonFileInstances.signedApk.deleteIfExists()
			throwErrorMessage(R.string.error_not_trustworthy_apk_patch)
		}
		if (!isUpdating) {
			uninstall()
		}
		progressMutable.makeIndeterminate()
		statusMutable.emit(R.string.status_installing)
		val installResult =
			PackageInstaller.installPackage(File(commonFileInstances.signedApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throwErrorMessage(R.string.error_install)
		}
		commonFileInstances.signedApk.deleteIfExists()
	}

	protected suspend inline fun uninstall() {
		statusMutable.emit(R.string.status_uninstalling)
		progressMutable.makeIndeterminate()
		val uninstallResult = PackageUninstaller.uninstallPackage(PACKAGE_NAME)
		if (!uninstallResult) {
			throwErrorMessage(R.string.error_uninstall)
		}
	}

	private suspend inline fun installBackup() {
		statusMutable.emit(R.string.status_installing)
		val installResult =
			PackageInstaller.installPackage(File(commonFileInstances.backupApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throwErrorMessage(R.string.error_install)
		}
	}

	protected suspend inline fun removeSignature(apkZip: ZipFile) {
		statusMutable.emit(R.string.status_removing_signature)
		withContext(ioDispatcher) {
			val apkProgressMonitor = apkZip.progressMonitor
			apkZip.removeFile("META-INF/")
			while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
				progressMutable.emit(
					apkProgressMonitor.workCompleted.toInt(),
					apkProgressMonitor.totalWork.toInt()
				)
				delay(30)
			}
		}
	}

	protected suspend inline fun extractSigningKey() {
		val assets = MainApplication.context.assets
		if (!privateKeyFile.exists()) {
			assets.copyAssetToFile("testkey.pk8", privateKeyFile)
		}
		if (!rsaTemplateFile.exists()) {
			assets.copyAssetToFile("testkey.past", rsaTemplateFile)
		}
	}

	protected suspend inline fun sign() {
		statusMutable.emit(R.string.status_signing_apk)
		extractSigningKey()
		progressMutable.makeIndeterminate()
		commonFileInstances.signedApk.deleteIfExists()
		commonFileInstances.signedApk.create()
		commonFileInstances.tempApk.createInputStream().use { tempApk ->
			commonFileInstances.signedApk.createOutputStream().use { signedApk ->
				PseudoApkSigner(rsaTemplateFile, privateKeyFile).sign(tempApk, signedApk)
			}
		}
		statusMutable.emit(R.string.status_writing_patched_apk_hash)
		Preferences.set(
			CommonFileHashKey.signed_apk_hash.name,
			commonFileInstances.signedApk.computeMd5()
		)
		commonFileInstances.tempApk.deleteIfExists()
	}

	protected suspend inline fun AssetManager.copyAssetToFile(assetName: String, file: File) {
		openFd(assetName).use { fileDescriptor ->
			fileDescriptor.createInputStream().use { inputStream ->
				FileOutputStream(file).use { outputStream ->
					ioService.copy(
						inputStream,
						outputStream,
						fileDescriptor.length,
						progressMutable
					)
				}
			}
		}
	}

	companion object {
		const val PACKAGE_NAME = "com.mages.chaoschild_jp"
	}
}