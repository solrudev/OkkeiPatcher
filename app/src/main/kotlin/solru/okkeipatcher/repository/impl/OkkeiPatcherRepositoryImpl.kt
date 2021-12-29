package solru.okkeipatcher.repository.impl

import androidx.core.os.ConfigurationCompat
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.R
import solru.okkeipatcher.api.OkkeiPatcherService
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.services.ObservableServiceImpl
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.OkkeiPatcherChangelog
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.io.utils.extensions.download
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import solru.okkeipatcher.utils.appVersionCode
import solru.okkeipatcher.utils.extensions.reset
import java.io.File
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(
	private val httpDownloader: HttpDownloader,
	private val okkeiPatcherService: OkkeiPatcherService
) : ObservableServiceImpl(), OkkeiPatcherRepository {

	private val updateFile = File(OkkeiStorage.private, APP_UPDATE_FILE_NAME)
	private var isUpdateDownloaded = false

	override suspend fun isUpdateAvailable() =
		okkeiPatcherService.getOkkeiPatcherData().version > appVersionCode

	override suspend fun getUpdateSizeInMb() =
		"%.2f".format(okkeiPatcherService.getOkkeiPatcherData().size / 1_048_576.0).toDouble()

	override suspend fun getUpdateFile(): File {
		if (isUpdateDownloaded && updateFile.exists()) {
			return updateFile
		}
		isUpdateDownloaded = false
		mutableStatus.emit(LocalizedString.resource(R.string.status_update_app_downloading))
		try {
			val updateData = okkeiPatcherService.getOkkeiPatcherData()
			val updateHash: String
			try {
				updateHash = httpDownloader.download(updateData.url, updateFile, hashing = true) { progressData ->
					progressPublisher.mutableProgress.emit(progressData)
				}
			} catch (e: Throwable) {
				throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (updateHash != updateData.hash) {
				throw OkkeiException(LocalizedString.resource(R.string.error_update_app_corrupted))
			}
		} catch (e: Throwable) {
			if (updateFile.exists()) updateFile.delete()
			withContext(NonCancellable) { mutableStatus.emit(LocalizedString.resource(R.string.status_aborted)) }
			throw e
		} finally {
			withContext(NonCancellable) { progressPublisher.mutableProgress.reset() }
		}
		isUpdateDownloaded = true
		return updateFile
	}

	override suspend fun getChangelog(): OkkeiPatcherChangelog {
		val locale = ConfigurationCompat.getLocales(OkkeiApplication.context.resources.configuration)[0]
		return okkeiPatcherService.getChangelog(locale.language)
	}
}