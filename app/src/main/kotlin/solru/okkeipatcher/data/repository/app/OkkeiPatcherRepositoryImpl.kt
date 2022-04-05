package solru.okkeipatcher.data.repository.app

import solru.okkeipatcher.R
import solru.okkeipatcher.data.network.api.OkkeiPatcherApi
import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.operation.AbstractOperation
import solru.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.io.util.extension.download
import solru.okkeipatcher.util.appVersionCode
import java.io.File
import java.util.*
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(
	private val httpDownloader: HttpDownloader,
	private val okkeiPatcherApi: OkkeiPatcherApi
) : OkkeiPatcherRepository {

	private val updateFile = File(OkkeiStorage.private, APP_UPDATE_FILE_NAME)
	private var isUpdateDownloaded = false

	override suspend fun isUpdateAvailable() =
		okkeiPatcherApi.getOkkeiPatcherData().version > appVersionCode

	override suspend fun getUpdateSizeInMb() = okkeiPatcherApi.getOkkeiPatcherData().size / 1_048_576.0

	override suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelogDto {
		return okkeiPatcherApi.getChangelog(locale.language)
	}

	override fun getUpdateFile() = object : AbstractOperation<File>() {

		override val progressMax = 100

		override suspend fun invoke(): File {
			if (isUpdateDownloaded && updateFile.exists()) {
				return updateFile
			}
			isUpdateDownloaded = false
			try {
				val updateData = okkeiPatcherApi.getOkkeiPatcherData()
				val updateHash = httpDownloader.download(updateData.url, updateFile, hashing = true) { progressDelta ->
					_progressDelta.emit(progressDelta)
				}
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
	}
}