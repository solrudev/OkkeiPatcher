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

package ru.solrudev.okkeipatcher.data.repository.patch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import ru.solrudev.okkeipatcher.data.core.Cache
import ru.solrudev.okkeipatcher.data.network.model.PatchFileDto
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.data.repository.patch.mapper.toPatchFileData
import ru.solrudev.okkeipatcher.domain.core.persistence.Retrievable
import ru.solrudev.okkeipatcher.domain.model.PatchFileData
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles

class PatchFilesImpl<T>(
	private val cache: Cache<T>,
	name: String,
	private val selector: (T) -> List<PatchFileDto>,
	private val patchStatus: Retrievable<Boolean>,
	preferences: DataStore<Preferences>
) : PatchFiles {

	private val versionKey = intPreferencesKey(name)
	override val installedVersion = Preference(key = versionKey, defaultValue = { 1 }, preferences)

	override suspend fun getData(refresh: Boolean): List<PatchFileData> {
		return selector(cache.retrieve(refresh)).map(PatchFileDto::toPatchFileData)
	}

	override suspend fun isUpdateAvailable(refresh: Boolean): Boolean {
		val isPatched = patchStatus.retrieve()
		if (!isPatched) {
			return false
		}
		val currentVersion = installedVersion.retrieve()
		val latestVersion = try {
			getData(refresh).maxOfOrNull { it.version } ?: 1
		} catch (_: NetworkNotAvailableException) {
			currentVersion
		}
		return latestVersion > currentVersion
	}

	override suspend fun getSizeInMb(): Double {
		val filesSize = getData().sumOf { it.size } / 1_048_576.0
		val isPatched = patchStatus.retrieve()
		if (!isPatched) {
			return filesSize
		}
		return if (isUpdateAvailable()) filesSize else 0.0
	}
}