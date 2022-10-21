package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageInstaller
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.util.download
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.exception.AppUpdateCorruptedException
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.repository.work.DownloadUpdateWorkRepository
import java.io.File
import java.util.*
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val okkeiPatcherApi: OkkeiPatcherApi,
	private val downloadUpdateWorkRepository: DownloadUpdateWorkRepository,
	private val fileDownloader: FileDownloader,
	private val packageInstaller: PackageInstaller
) : OkkeiPatcherRepository {

	private val changelogCache = InMemoryCache {
		val locale = ConfigurationCompat.getLocales(applicationContext.resources.configuration)[0] ?: Locale.ENGLISH
		okkeiPatcherApi.getChangelog(applicationContext.versionCode, locale.language)
	}

	private val okkeiPatcherDataCache = InMemoryCache(okkeiPatcherApi::getOkkeiPatcherData)
	private val updateFile = File(applicationContext.filesDir, APP_UPDATE_FILE_NAME)
	private val _isUpdateAvailable = MutableStateFlow(false)
	override val isUpdateAvailable = _isUpdateAvailable.asStateFlow()

	override suspend fun getUpdateData(refresh: Boolean) = try {
		val okkeiPatcherData = okkeiPatcherDataCache.retrieve(refresh)
		val changelog = changelogCache.retrieve(refresh)
		val isUpdateAvailable = okkeiPatcherData.version > applicationContext.versionCode
		_isUpdateAvailable.value = isUpdateAvailable
		OkkeiPatcherUpdateData(
			isAvailable = isUpdateAvailable,
			sizeInMb = okkeiPatcherData.size / 1_048_576.0,
			changelog = changelog.map { OkkeiPatcherVersion(it.versionName, it.changes) }
		)
	} catch (_: Throwable) {
		OkkeiPatcherUpdateData(isAvailable = false, sizeInMb = 0.0, changelog = emptyList())
	}

	override suspend fun enqueueUpdateDownloadWork(): Work {
		return downloadUpdateWorkRepository.enqueueWork()
	}

	override suspend fun installUpdate() = packageInstaller.install(updateFile, immediate = true)

	override fun downloadUpdate() = operation(progressMax = fileDownloader.progressMax) {
		wrapDomainExceptions {
			try {
				val updateData = okkeiPatcherApi.getOkkeiPatcherData()
				val updateHash =
					fileDownloader.download(updateData.url, updateFile, ioDispatcher, hashing = true, ::progressDelta)
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
		}
	}

	override fun getPendingUpdateDownloadWorkFlow(): Flow<Work> {
		return downloadUpdateWorkRepository.getPendingWorkFlow()
	}
}