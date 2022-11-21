package ru.solrudev.okkeipatcher.data.repository.app

import io.github.solrudev.simpleinstaller.PackageInstaller
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.exception.AppUpdateCorruptedException
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.repository.work.DownloadUpdateWorkRepository
import javax.inject.Inject

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(
	private val environment: OkkeiEnvironment,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val okkeiPatcherApi: OkkeiPatcherApi,
	private val downloadUpdateWorkRepository: DownloadUpdateWorkRepository,
	private val fileDownloader: FileDownloader,
	private val packageInstaller: PackageInstaller,
	private val fileSystem: FileSystem
) : OkkeiPatcherRepository {

	private val changelogCache = InMemoryCache {
		okkeiPatcherApi.getChangelog(environment.versionCode, environment.locale.language)
	}

	private val okkeiPatcherDataCache = InMemoryCache(okkeiPatcherApi::getOkkeiPatcherData)
	private val updateFile = environment.filesPath / APP_UPDATE_FILE_NAME
	private val _isUpdateAvailable = MutableStateFlow(false)
	override val isUpdateAvailable = _isUpdateAvailable.asStateFlow()

	override suspend fun getUpdateData(refresh: Boolean) = try {
		val okkeiPatcherData = okkeiPatcherDataCache.retrieve(refresh)
		val changelog = changelogCache.retrieve(refresh)
		val isUpdateAvailable = okkeiPatcherData.version > environment.versionCode
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

	override suspend fun installUpdate() = try {
		packageInstaller.install(updateFile.toFile(), immediate = true)
	} catch (t: Throwable) {
		Result.Failure(LocalizedString.raw(t.stackTraceToString()))
	}

	override fun downloadUpdate() = operation(progressMax = fileDownloader.progressMax) {
		wrapDomainExceptions {
			try {
				val updateData = okkeiPatcherApi.getOkkeiPatcherData()
				val sink = withContext(ioDispatcher) {
					fileSystem.prepareRecreate(updateFile)
					fileSystem.sink(updateFile)
				}
				val updateHash = fileDownloader.download(
					updateData.url, sink, hashing = true, onProgressDeltaChanged = ::progressDelta
				)
				if (updateHash != updateData.hash) {
					throw AppUpdateCorruptedException()
				}
			} catch (t: Throwable) {
				if (fileSystem.exists(updateFile)) {
					fileSystem.delete(updateFile)
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