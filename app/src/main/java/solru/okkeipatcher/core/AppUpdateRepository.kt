package solru.okkeipatcher.core

import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.extensions.computeHash
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.pm.PackageManager
import java.io.File
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class AppUpdateRepository @Inject constructor(private val ioService: IoService) : AppServiceBase() {

	private val appUpdateFile = File(OkkeiStorage.private, APP_UPDATE_FILE_NAME)
	private var isAppUpdateDownloaded = false

	fun isAppUpdateAvailable(manifest: OkkeiManifest) =
		manifest.okkeiPatcher.version > PackageManager.versionCode

	suspend fun getAppUpdate(manifest: OkkeiManifest): File {
		if (isAppUpdateDownloaded) {
			return appUpdateFile
		}
		isRunning = true
		statusMutable.emit(R.string.status_update_app_downloading)
		tryWrapper(onCatch = { if (appUpdateFile.exists()) appUpdateFile.delete() }) {
			ioService.downloadAndWrapException(manifest.okkeiPatcher.url, appUpdateFile)
			statusMutable.emit(R.string.status_comparing_apk)
			val updateHash = ioService.computeHash(appUpdateFile, progressMutable)
			if (updateHash != manifest.okkeiPatcher.hash) {
				throwErrorMessage(R.string.error_update_app_corrupted)
			}
		}
		isAppUpdateDownloaded = true
		return appUpdateFile
	}
}