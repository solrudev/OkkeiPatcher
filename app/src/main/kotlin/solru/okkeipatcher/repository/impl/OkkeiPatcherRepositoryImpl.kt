package solru.okkeipatcher.repository.impl

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.services.ObservableServiceImpl
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.io.utils.extensions.download
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import solru.okkeipatcher.utils.appVersionCode
import solru.okkeipatcher.utils.extensions.reset
import java.io.File
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(private val httpDownloader: HttpDownloader) :
	ObservableServiceImpl(), OkkeiPatcherRepository {

	private val appUpdateFile = File(OkkeiStorage.private, APP_UPDATE_FILE_NAME)
	private var isAppUpdateDownloaded = false

	override fun isAppUpdateAvailable(manifest: OkkeiManifest) =
		manifest.okkeiPatcher.version > appVersionCode

	override fun appUpdateSizeInMb(manifest: OkkeiManifest) =
		"%.2f".format(manifest.okkeiPatcher.size / 1_048_576.0).toDouble()

	override suspend fun getAppUpdate(manifest: OkkeiManifest): File {
		if (isAppUpdateDownloaded) {
			return appUpdateFile
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_update_app_downloading))
		try {
			val updateHash: String
			try {
				updateHash = httpDownloader.download(manifest.okkeiPatcher.url, appUpdateFile, hashing = true)
				{ progressData -> progressPublisher.mutableProgress.emit(progressData) }
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
			withContext(NonCancellable) { progressPublisher.mutableProgress.reset() }
		}
		isAppUpdateDownloaded = true
		return appUpdateFile
	}
}