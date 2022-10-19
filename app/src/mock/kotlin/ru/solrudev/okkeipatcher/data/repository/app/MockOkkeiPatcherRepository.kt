package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.installPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.copy
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.repository.work.DownloadUpdateWorkRepository
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class MockOkkeiPatcherRepository @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val downloadUpdateWorkRepository: DownloadUpdateWorkRepository,
	private val streamCopier: StreamCopier,
	private val packageInstaller: PackageInstaller
) : OkkeiPatcherRepository {

	private var checksCount = 0

	private val changelog = listOf(
		OkkeiPatcherVersion(versionName = "1.0", changes = listOf("Change 1", "Change 2")),
		OkkeiPatcherVersion(versionName = "1.1", changes = listOf("Change 1")),
		OkkeiPatcherVersion(versionName = "1.2", changes = listOf("Change 1", "Change 2", "Change 3"))
	)

	private val updateData = OkkeiPatcherUpdateData(isAvailable = true, sizeInMb = 3.14, changelog)
	private val updateFile = File(applicationContext.filesDir, APP_UPDATE_FILE_NAME)
	private val _isUpdateAvailable = MutableStateFlow(false)
	override val isUpdateAvailable = _isUpdateAvailable.asStateFlow()

	override suspend fun getUpdateData(refresh: Boolean): OkkeiPatcherUpdateData {
		checksCount++
		if (refresh) {
			delay(3.seconds)
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

	override suspend fun installUpdate() {
		packageInstaller.installPackage(updateFile) {
			confirmationStrategy = ConfirmationStrategy.IMMEDIATE
		}
	}

	override fun downloadUpdate() = operation(progressMax = streamCopier.progressMax) {
		wrapDomainExceptions {
			try {
				val installedApkPath = applicationContext.packageManager
					.getPackageInfo(applicationContext.packageName, 0)
					.applicationInfo
					.publicSourceDir
				val installedApk = File(installedApkPath)
				streamCopier.copy(installedApk, updateFile, ioDispatcher, onProgressDeltaChanged = ::progressDelta)
			} catch (t: Throwable) {
				if (updateFile.exists()) {
					updateFile.delete()
				}
				throw t
			}
		}
	}

	override fun getPendingUpdateDownloadWorkFlow(): Flow<Work> {
		return downloadUpdateWorkRepository.getPendingWorkFlow()
	}
}