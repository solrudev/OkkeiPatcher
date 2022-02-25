package solru.okkeipatcher.repository.impl

import solru.okkeipatcher.R
import solru.okkeipatcher.api.OkkeiPatcherService
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.OkkeiPatcherChangelog
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.services.ObservableServiceImpl
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.io.utils.extensions.download
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import solru.okkeipatcher.utils.appVersionCode
import solru.okkeipatcher.utils.extensions.round
import java.io.File
import java.util.*
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
		(okkeiPatcherService.getOkkeiPatcherData().size / 1_048_576.0).round(2)

	override suspend fun getUpdateFile(): File {
		if (isUpdateDownloaded && updateFile.exists()) {
			return updateFile
		}
		isUpdateDownloaded = false
		_status.emit(LocalizedString.resource(R.string.status_update_app_downloading))
		try {
			val updateData = okkeiPatcherService.getOkkeiPatcherData()
			val updateHash: String
			try {
				updateHash = httpDownloader.download(updateData.url, updateFile, hashing = true) { progressData ->
					progressPublisher._progress.emit(progressData)
				}
			} catch (e: Throwable) {
				throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
			}
			_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (updateHash != updateData.hash) {
				throw OkkeiException(LocalizedString.resource(R.string.error_update_app_corrupted))
			}
		} catch (e: Throwable) {
			if (updateFile.exists()) {
				updateFile.delete()
			}
			throw e
		}
		isUpdateDownloaded = true
		return updateFile
	}

	override suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelog {
		return okkeiPatcherService.getChangelog(locale.language)
	}
}