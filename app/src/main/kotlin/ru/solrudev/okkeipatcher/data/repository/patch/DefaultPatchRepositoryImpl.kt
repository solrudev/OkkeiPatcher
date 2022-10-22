package ru.solrudev.okkeipatcher.data.repository.patch

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.api.patch.DefaultPatchApi
import ru.solrudev.okkeipatcher.data.repository.patch.mapper.toPatchFileData
import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultPatchRepositoryImpl @Inject constructor(
	defaultPatchApi: DefaultPatchApi,
	preferencesRepository: PreferencesRepository,
	@ApplicationContext applicationContext: Context
) : DefaultPatchRepository {

	private val Context.dataStore by preferencesDataStore(name = "patch_files_en_hash")
	private val preferences = applicationContext.dataStore
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