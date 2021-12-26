package solru.okkeipatcher.repository

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.services.ObservableServiceImpl
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.IoService
import solru.okkeipatcher.io.utils.extensions.download
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.appVersionCode
import solru.okkeipatcher.utils.extensions.reset
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

@Singleton
class AppUpdateRepository @Inject constructor(private val ioService: IoService) : ObservableServiceImpl() {

	private val appUpdateFile = File(OkkeiStorage.private, APP_UPDATE_FILE_NAME)
	private var isAppUpdateDownloaded = false

	fun isAppUpdateAvailable(manifest: OkkeiManifest) =
		manifest.okkeiPatcher.version > appVersionCode

	fun appUpdateSizeInMb(manifest: OkkeiManifest) =
		"%.2f".format(manifest.okkeiPatcher.size / 1_048_576.0).toDouble()

	suspend fun getAppUpdate(manifest: OkkeiManifest): File {
		if (isAppUpdateDownloaded) {
			return appUpdateFile
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_update_app_downloading))
		try {
			val updateHash: String
			try {
				updateHash = ioService.download(manifest.okkeiPatcher.url, appUpdateFile, hashing = true)
				{ progressData -> progressProvider.mutableProgress.emit(progressData) }
			} catch (e: Throwable) {
				throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (updateHash != manifest.okkeiPatcher.hash) {
				throw OkkeiException(LocalizedString.resource(R.string.error_update_app_corrupted))
			}
		} catch (e: Throwable) {
			if (appUpdateFile.exists()) appUpdateFile.delete()
			withContext(NonCancellable) { mutableStatus.emit(LocalizedString.resource(R.string.status_aborted)) }
			throw e
		} finally {
			withContext(NonCancellable) { progressProvider.mutableProgress.reset() }
		}
		isAppUpdateDownloaded = true
		return appUpdateFile
	}
}