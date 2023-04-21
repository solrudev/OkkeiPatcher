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
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.api.patch.DefaultPatchApi
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.data.repository.patch.mapper.toPatchFileData
import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPatchRepositoryImpl @Inject constructor(
	defaultPatchApi: DefaultPatchApi,
	preferencesRepository: PreferencesRepository,
	preferencesDataStoreFactory: PreferencesDataStoreFactory
) : DefaultPatchRepository {

	private val preferences = preferencesDataStoreFactory.create("patch_files_en")
	private val patchDataCache = InMemoryCache(defaultPatchApi::getPatchData)

	override val scripts = PatchFileImpl(
		cache = patchDataCache,
		name = "scripts",
		selector = { it.scripts.toPatchFileData() },
		patchStatus = preferencesRepository.patchStatus,
		preferences
	)

	override val obb = PatchFileImpl(
		cache = patchDataCache,
		name = "obb",
		selector = { it.obb.toPatchFileData() },
		patchStatus = preferencesRepository.patchStatus,
		preferences
	)

	override suspend fun getDisplayVersion() = try {
		patchDataCache.retrieve().displayVersion
	} catch (_: Throwable) {
		""
	}

	override suspend fun getPatchUpdates(refresh: Boolean) = DefaultPatchUpdates(
		scripts.isUpdateAvailable(refresh),
		obb.isUpdateAvailable(refresh)
	)

	override suspend fun getPatchSizeInMb() = try {
		scripts.getSizeInMb() + obb.getSizeInMb()
	} catch (_: Throwable) {
		-1.0
	}

	override suspend fun clearPersistedData() {
		preferences.edit {
			it.clear()
		}
	}
}