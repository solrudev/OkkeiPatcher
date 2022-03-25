package solru.okkeipatcher.data.repository

import kotlinx.coroutines.CancellationException
import solru.okkeipatcher.R
import solru.okkeipatcher.data.network.api.OkkeiPatcherApi
import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.base.ObservableImpl
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.repository.OkkeiPatcherRepository
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.io.util.extension.download
import solru.okkeipatcher.util.appVersionCode
import solru.okkeipatcher.util.extension.round
import java.io.File
import java.util.*
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(
	private val httpDownloader: HttpDownloader,
	private val okkeiPatcherApi: OkkeiPatcherApi
) : ObservableImpl(), OkkeiPatcherRepository {

	private val updateFile = File(OkkeiStorage.private, APP_UPDATE_FILE_NAME)
	private var isUpdateDownloaded = false

	override suspend fun isUpdateAvailable() =
		okkeiPatcherApi.getOkkeiPatcherData().version > appVersionCode

	override suspend fun getUpdateSizeInMb() =
		(okkeiPatcherApi.getOkkeiPatcherData().size / 1_048_576.0).round(2)

	override suspend fun getUpdateFile(): File {
		if (isUpdateDownloaded && updateFile.exists()) {
			return updateFile
		}
		isUpdateDownloaded = false
		_status.emit(LocalizedString.resource(R.string.status_update_app_downloading))
		try {
			val updateData = okkeiPatcherApi.getOkkeiPatcherData()
			val updateHash: String
			try {
				updateHash = httpDownloader.download(updateData.url, updateFile, hashing = true) { progressData ->
					progressPublisher._progress.emit(progressData)
				}
			} catch (t: Throwable) {
				if (t is CancellationException) {
					throw t
				}
				throw LocalizedException(LocalizedString.resource(R.string.error_http_file_download), cause = t)
			}
			_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
			if (updateHash != updateData.hash) {
				throw LocalizedException(LocalizedString.resource(R.string.error_update_app_corrupted))
			}
		} catch (t: Throwable) {
			if (updateFile.exists()) {
				updateFile.delete()
			}
			throw t
		}
		isUpdateDownloaded = true
		return updateFile
	}

	override suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelogDto {
		return okkeiPatcherApi.getChangelog(locale.language)
	}
}