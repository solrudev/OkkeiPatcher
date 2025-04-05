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

import androidx.datastore.preferences.core.edit
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.api.patch.PatchApi
import ru.solrudev.okkeipatcher.data.network.api.patch.getPatchData
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.service.GameInstallationProvider
import ru.solrudev.okkeipatcher.domain.model.PatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository

class PatchRepositoryImpl(
	patchApi: PatchApi,
	patchStateRepository: PatchStateRepository,
	gameInstallationProvider: GameInstallationProvider,
	preferencesDataStoreFactory: PreferencesDataStoreFactory,
	dataStoreName: String
) : PatchRepository {

	private val preferences = preferencesDataStoreFactory.create(
		name = dataStoreName,
		migrations = listOf(Migration_PatchFiles_0_1)
	)

	private val patchDataCache = InMemoryCache {
		patchApi.getPatchData(gameInstallationProvider.getVersionCode())
	}

	override val apkPatchFiles = PatchFilesImpl(
		cache = patchDataCache,
		name = "apk",
		selector = { it.apk },
		patchStatus = patchStateRepository.patchStatus,
		preferences
	)

	override val obbPatchFiles = PatchFilesImpl(
		cache = patchDataCache,
		name = "obb",
		selector = { it.obb },
		patchStatus = patchStateRepository.patchStatus,
		preferences
	)

	override suspend fun getDisplayVersion() = try {
		patchDataCache.retrieve().displayVersion
	} catch (_: Throwable) {
		""
	}

	override suspend fun getPatchUpdates(refresh: Boolean) = PatchUpdates(
		apkPatchFiles.isUpdateAvailable(refresh),
		obbPatchFiles.isUpdateAvailable(refresh = false)
	)

	override suspend fun getPatchSizeInMb() = try {
		apkPatchFiles.getSizeInMb() + obbPatchFiles.getSizeInMb()
	} catch (_: Throwable) {
		-1.0
	}

	override suspend fun clearPersistedData() {
		preferences.edit {
			it.clear()
		}
	}
}