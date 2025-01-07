/*
 * Okkei Patcher
 * Copyright (C) 2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.network.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherVersionDto
import ru.solrudev.okkeipatcher.data.service.OkkeiPatcherApkProvider
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Singleton
class MockOkkeiPatcherApi @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val fileSystem: FileSystem,
	private val okkeiPatcherApkProvider: OkkeiPatcherApkProvider
) : OkkeiPatcherApi {

	private val changelog = listOf(
		OkkeiPatcherVersionDto(versionName = "1.0", changes = listOf("Change 1", "Change 2")),
		OkkeiPatcherVersionDto(versionName = "1.1", changes = listOf("Change 1")),
		OkkeiPatcherVersionDto(versionName = "1.2", changes = listOf("Change 1", "Change 2", "Change 3"))
	)

	private val changelogRu = listOf(
		OkkeiPatcherVersionDto(versionName = "1.0", changes = listOf("Изменение 1", "Изменение 2")),
		OkkeiPatcherVersionDto(versionName = "1.1", changes = listOf("Изменение 1")),
		OkkeiPatcherVersionDto(versionName = "1.2", changes = listOf("Изменение 1", "Изменение 2", "Изменение 3"))
	)

	private var appEndpointHitsCount = 0

	override suspend fun getOkkeiPatcherData(): FileDto {
		val delayDuration = if (appEndpointHitsCount > 0) 150.milliseconds else 1.seconds
		delay(delayDuration)
		if (appEndpointHitsCount++ < 1) {
			return FileDto(version = 0, url = "", hash = "", size = 0L)
		}
		val installedApk = okkeiPatcherApkProvider.getOkkeiPatcherApkPath()
		val hash = runInterruptible(ioDispatcher) { fileSystem.computeHash(installedApk) }
		val size = fileSystem.metadata(installedApk).size ?: 0L
		return FileDto(version = Int.MAX_VALUE, url = installedApk.toString(), hash, size)
	}

	override suspend fun getChangelog(currentVersion: Int, language: String): List<OkkeiPatcherVersionDto> {
		delay(1.seconds)
		if (appEndpointHitsCount < 1) {
			return emptyList()
		}
		return when {
			language.startsWith("ru", ignoreCase = true) -> changelogRu
			else -> changelog
		}
	}
}