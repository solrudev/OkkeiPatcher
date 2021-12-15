package solru.okkeipatcher.core.files.base

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
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFileInstances
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.getPackagePublicSourceDir
import solru.okkeipatcher.utils.isPackageInstalled
import java.io.File
import java.io.FileOutputStream

abstract class Apk(
	protected val commonFileInstances: CommonFileInstances,
	protected val ioService: IoService,
	protected val ioDispatcher: CoroutineDispatcher
) : AppServiceBase(ProgressProviderImpl()), PatchableGameFile {

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
		progressProvider.mutableProgress
	)

	override suspend fun update(manifest: OkkeiManifest) {
		try {
			isUpdating = true
			progressProvider.mutableProgress.reset()
			commonFileInstances.tempApk.delete()
			commonFileInstances.signedApk.delete()
			patch(manifest)
		} finally {
			isUpdating = false
		}
	}

	override fun deleteBackup() {
		commonFileInstances.backupApk.delete()
	}

	override suspend fun backup() =
		tryWrapper(onCatch = { commonFileInstances.backupApk.delete() }) {
			progressProvider.mutableProgress.reset()
			if (!isPackageInstalled(PACKAGE_NAME)) {
				throwErrorMessage(R.string.error_game_not_found)
			}
			statusMutable.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (verifyBackupIntegrity()) return
			statusMutable.emit(LocalizedString.resource(R.string.status_backing_up_apk))
			copyOriginalApkTo(commonFileInstances.backupApk)
			statusMutable.emit(LocalizedString.resource(R.string.status_writing_apk_hash))
			Preferences.set(
				CommonFileHashKey.backup_apk_hash.name,
				commonFileInstances.backupApk.computeHash()
			)
		}

	override suspend fun restore() = tryWrapper {
		progressProvider.mutableProgress.reset()
		if (!commonFileInstances.backupApk.exists) {
			throwErrorMessage(R.string.error_apk_not_found_restore)
		}
		statusMutable.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFileInstances.backupApk.verify()) {
			throwErrorMessage(R.string.error_not_trustworthy_apk_restore)
		}
		statusMutable.emit(LocalizedString.resource(R.string.empty))
		if (isPackageInstalled(PACKAGE_NAME)) {
			uninstall()
		}
		installBackup()
	}

	override suspend fun verifyBackupIntegrity() = commonFileInstances.backupApk.verify()

	protected suspend inline fun copyOriginalApkTo(destinationFile: solru.okkeipatcher.io.base.File) = coroutineScope {
		statusMutable.emit(LocalizedString.resource(R.string.status_copying_apk))
		val progressJob = launch {
			progressProvider.mutableProgress.emitAll(originalApk.progress)
		}
		originalApk.copyTo(destinationFile)
		progressJob.cancel()
	}

	protected suspend inline fun installPatched() {
		if (!commonFileInstances.signedApk.exists) {
			throwErrorMessage(R.string.error_apk_not_found_patch)
		}
		statusMutable.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (!commonFileInstances.signedApk.verify()) {
			commonFileInstances.signedApk.delete()
			throwErrorMessage(R.string.error_not_trustworthy_apk_patch)
		}
		if (!isUpdating) {
			uninstall()
		}
		progressProvider.mutableProgress.makeIndeterminate()
		statusMutable.emit(LocalizedString.resource(R.string.status_installing))
		val installResult =
			PackageInstaller.installPackage(File(commonFileInstances.signedApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throwErrorMessage(R.string.error_install)
		}
		commonFileInstances.signedApk.delete()
	}

	protected suspend inline fun uninstall() {
		statusMutable.emit(LocalizedString.resource(R.string.status_uninstalling))
		progressProvider.mutableProgress.makeIndeterminate()
		val uninstallResult = PackageUninstaller.uninstallPackage(PACKAGE_NAME)
		if (!uninstallResult) {
			throwErrorMessage(R.string.error_uninstall)
		}
	}

	private suspend inline fun installBackup() {
		statusMutable.emit(LocalizedString.resource(R.string.status_installing))
		val installResult =
			PackageInstaller.installPackage(File(commonFileInstances.backupApk.fullPath))
		if (installResult is InstallResult.Failure) {
			throwErrorMessage(R.string.error_install)
		}
	}

	protected suspend inline fun removeSignature(apkZip: ZipFile) {
		statusMutable.emit(LocalizedString.resource(R.string.status_removing_signature))
		withContext(ioDispatcher) {
			val apkProgressMonitor = apkZip.progressMonitor
			apkZip.removeFile("META-INF/")
			while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
				progressProvider.mutableProgress.emit(
					apkProgressMonitor.workCompleted.toInt(),
					apkProgressMonitor.totalWork.toInt()
				)
				delay(30)
			}
		}
	}

	protected suspend inline fun extractSigningKey() {
		val assets = OkkeiApplication.context.assets
		if (!privateKeyFile.exists()) {
			assets.copyAssetToFile("testkey.pk8", privateKeyFile)
		}
		if (!rsaTemplateFile.exists()) {
			assets.copyAssetToFile("testkey.past", rsaTemplateFile)
		}
	}

	protected suspend inline fun sign() {
		statusMutable.emit(LocalizedString.resource(R.string.status_signing_apk))
		extractSigningKey()
		progressProvider.mutableProgress.makeIndeterminate()
		commonFileInstances.signedApk.delete()
		commonFileInstances.signedApk.create()
		commonFileInstances.tempApk.createInputStream().use { tempApk ->
			commonFileInstances.signedApk.createOutputStream().use { signedApk ->
				PseudoApkSigner(rsaTemplateFile, privateKeyFile).sign(tempApk, signedApk)
			}
		}
		statusMutable.emit(LocalizedString.resource(R.string.status_writing_patched_apk_hash))
		Preferences.set(
			CommonFileHashKey.signed_apk_hash.name,
			commonFileInstances.signedApk.computeHash()
		)
		commonFileInstances.tempApk.delete()
	}

	protected suspend inline fun AssetManager.copyAssetToFile(assetName: String, file: File) {
		openFd(assetName).use { fileDescriptor ->
			fileDescriptor.createInputStream().use { inputStream ->
				FileOutputStream(file).use { outputStream ->
					ioService.copy(
						inputStream,
						outputStream,
						fileDescriptor.length,
						progressProvider.mutableProgress
					)
				}
			}
		}
	}

	companion object {
		const val PACKAGE_NAME = "com.mages.chaoschild_jp"
	}
}