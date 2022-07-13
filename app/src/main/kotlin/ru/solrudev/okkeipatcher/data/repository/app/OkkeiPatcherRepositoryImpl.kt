package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import ru.solrudev.okkeipatcher.data.util.download
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.service.FileDownloader
import java.io.File
import java.util.*
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

// TODO
class OkkeiPatcherRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	private val fileDownloader: FileDownloader,
	private val okkeiPatcherApi: OkkeiPatcherApi
) : OkkeiPatcherRepository {

	private val updateFile = File(applicationContext.filesDir, APP_UPDATE_FILE_NAME)
	private var isUpdateDownloaded = false

	override suspend fun isUpdateAvailable() =
		okkeiPatcherApi.getOkkeiPatcherData().version > applicationContext.versionCode

	override suspend fun getUpdateSizeInMb() = okkeiPatcherApi.getOkkeiPatcherData().size / 1_048_576.0

	override suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelogDto {
		return okkeiPatcherApi.getChangelog(locale.language)
	}

	override fun getUpdateFile() = operation(progressMax = fileDownloader.progressMax) {
		if (isUpdateDownloaded && updateFile.exists()) {
			return@operation updateFile
		}
		isUpdateDownloaded = false
		try {
			val updateData = okkeiPatcherApi.getOkkeiPatcherData()
			val updateHash = fileDownloader.download(updateData.url, updateFile, hashing = true, ::progressDelta)
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
		return@operation updateFile
	}
}