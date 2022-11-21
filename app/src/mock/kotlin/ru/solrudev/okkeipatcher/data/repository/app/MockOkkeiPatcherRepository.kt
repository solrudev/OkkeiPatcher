package ru.solrudev.okkeipatcher.data.repository.app

import io.github.solrudev.simpleinstaller.PackageInstaller
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.OkkeiPatcherApkProvider
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.copy
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.repository.work.DownloadUpdateWorkRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class MockOkkeiPatcherRepository @Inject constructor(
	environment: OkkeiEnvironment,
	private val okkeiPatcherApkProvider: OkkeiPatcherApkProvider,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val downloadUpdateWorkRepository: DownloadUpdateWorkRepository,
	private val packageInstaller: PackageInstaller,
	private val fileSystem: FileSystem
) : OkkeiPatcherRepository {

	private var checksCount = 0

	private val changelog = listOf(
		OkkeiPatcherVersion(versionName = "1.0", changes = listOf("Change 1", "Change 2")),
		OkkeiPatcherVersion(versionName = "1.1", changes = listOf("Change 1")),
		OkkeiPatcherVersion(versionName = "1.2", changes = listOf("Change 1", "Change 2", "Change 3"))
	)

	private val updateData = OkkeiPatcherUpdateData(isAvailable = true, sizeInMb = 3.14, changelog)
	private val updateFile = environment.filesPath / APP_UPDATE_FILE_NAME
	private val _isUpdateAvailable = MutableStateFlow(false)
	override val isUpdateAvailable = _isUpdateAvailable.asStateFlow()

	override suspend fun getUpdateData(refresh: Boolean): OkkeiPatcherUpdateData {
		checksCount++
		if (refresh) {
			delay(2.seconds)
		}
		if (checksCount < 3) {
			return OkkeiPatcherUpdateData(isAvailable = false, sizeInMb = 0.0, changelog = emptyList())
		}
		_isUpdateAvailable.value = updateData.isAvailable
		return updateData
	}

	override suspend fun enqueueUpdateDownloadWork(): Work {
		return downloadUpdateWorkRepository.enqueueWork()
	}

	override suspend fun installUpdate() = try {
		packageInstaller.install(updateFile.toFile(), immediate = true)
	} catch (t: Throwable) {
		Result.Failure(LocalizedString.raw(t.stackTraceToString()))
	}

	override fun downloadUpdate() = operation(progressMax = STREAM_COPY_PROGRESS_MAX) {
		wrapDomainExceptions {
			try {
				withContext(ioDispatcher) {
					val installedApk = okkeiPatcherApkProvider.getOkkeiPatcherApkPath()
					fileSystem.copy(installedApk, updateFile, onProgressDeltaChanged = { progressDelta(it) })
				}
			} catch (t: Throwable) {
				if (fileSystem.exists(updateFile)) {
					fileSystem.delete(updateFile)
				}
				throw t
			}
		}
	}

	override fun getPendingUpdateDownloadWorkFlow(): Flow<Work> {
		return downloadUpdateWorkRepository.getPendingWorkFlow()
	}
}