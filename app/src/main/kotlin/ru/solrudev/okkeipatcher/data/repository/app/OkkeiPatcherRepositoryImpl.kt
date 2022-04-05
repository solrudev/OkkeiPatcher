package ru.solrudev.okkeipatcher.data.repository.app

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.domain.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.io.service.HttpDownloader
import ru.solrudev.okkeipatcher.io.util.extension.download
import ru.solrudev.okkeipatcher.util.appVersionCode
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