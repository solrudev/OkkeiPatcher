/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.repository.app

import io.github.solrudev.simpleinstaller.PackageInstaller
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okio.FileSystem
import ru.solrudev.okkeipatcher.app.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.app.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.OkkeiPatcherApkProvider
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.copy
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Singleton
class MockOkkeiPatcherRepository @Inject constructor(
	environment: OkkeiEnvironment,
	private val okkeiPatcherApkProvider: OkkeiPatcherApkProvider,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
	private val updateFile = environment.externalFilesPath / APP_UPDATE_FILE_NAME
	private val _isUpdateAvailable = MutableStateFlow(false)
	private val _isUpdateInstallPending = MutableStateFlow(fileSystem.exists(updateFile))
	override val isUpdateAvailable = _isUpdateAvailable.asStateFlow()
	override val isUpdateInstallPending = _isUpdateInstallPending.asStateFlow()

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

	override suspend fun installUpdate() = try {
		packageInstaller.install(updateFile, immediate = true)
	} catch (cancellationException: CancellationException) {
		throw cancellationException
	} catch (t: Throwable) {
		println(t.stackTraceToString())
		Result.failure(LocalizedString.empty())
	} finally {
		_isUpdateInstallPending.value = false
		fileSystem.delete(updateFile)
	}

	override fun downloadUpdate() = operation(progressMax = STREAM_COPY_PROGRESS_MAX) {
		wrapDomainExceptions {
			try {
				withContext(ioDispatcher) {
					val installedApk = okkeiPatcherApkProvider.getOkkeiPatcherApkPath()
					fileSystem.copy(installedApk, updateFile, onProgressDeltaChanged = {
						delay(50.milliseconds) // simulate long operation
						progressDelta(it)
					})
				}
				_isUpdateInstallPending.value = true
			} catch (t: Throwable) {
				fileSystem.delete(updateFile)
				throw t
			}
		}
	}
}