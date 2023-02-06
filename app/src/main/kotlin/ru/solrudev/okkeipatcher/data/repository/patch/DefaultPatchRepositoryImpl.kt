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

	override suspend fun getPatchUpdates() = DefaultPatchUpdates(
		scripts.isUpdateAvailable(),
		obb.isUpdateAvailable()
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