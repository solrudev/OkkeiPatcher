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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.FileSystem
import ru.solrudev.okkeipatcher.app.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.app.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.app.repository.work.DownloadUpdateWorkRepository
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.AppUpdateCorruptedException
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import java.util.concurrent.CancellationException
import javax.inject.Inject

const val APP_UPDATE_FILE_NAME = "OkkeiPatcher.apk"

class OkkeiPatcherRepositoryImpl @Inject constructor(
	private val environment: OkkeiEnvironment,
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
	private val updateFile = environment.externalFilesPath / APP_UPDATE_FILE_NAME
	private val _isUpdateAvailable = MutableStateFlow(false)
	private val _isUpdateInstallPending = MutableStateFlow(fileSystem.exists(updateFile))
	override val isUpdateAvailable = _isUpdateAvailable.asStateFlow()
	override val isUpdateInstallPending = _isUpdateInstallPending.asStateFlow()

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

	override fun downloadUpdate() = operation(progressMax = fileDownloader.progressMax) {
		wrapDomainExceptions {
			try {
				val updateData = okkeiPatcherDataCache.retrieve()
				val updateHash = fileDownloader.download(
					updateData.url, updateFile, hashing = true, onProgressDeltaChanged = ::progressDelta
				)
				if (updateHash != updateData.hash) {
					throw AppUpdateCorruptedException()
				}
				_isUpdateInstallPending.value = true
			} catch (t: Throwable) {
				fileSystem.delete(updateFile)
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