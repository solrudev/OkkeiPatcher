package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.util.download
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.AppUpdateCorruptedException
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
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

	override suspend fun isUpdateAvailable() = try {
		okkeiPatcherApi.getOkkeiPatcherData().version > applicationContext.versionCode
	} catch (t: Throwable) {
		false
	}

	override suspend fun getUpdateSizeInMb() = try {
		okkeiPatcherApi.getOkkeiPatcherData().size / 1_048_576.0
	} catch (t: Throwable) {
		-1.0
	}

	override suspend fun getChangelog(locale: Locale) = try {
		okkeiPatcherApi.getChangelog(locale.language, applicationContext.versionCode)
	} catch (t: Throwable) {
		OkkeiPatcherChangelogDto(emptyMap())
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
				throw AppUpdateCorruptedException()
			}
		} catch (t: Throwable) {
			if (updateFile.exists()) {
				updateFile.delete()
			}
			if (t is NetworkNotAvailableException) {
				throw NoNetworkException()
			}
			throw t
		}
		isUpdateDownloaded = true
		return@operation updateFile
	}
}