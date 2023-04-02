package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.PatchFileData
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates

interface PatchRepository {
	suspend fun getDisplayVersion(): String
	suspend fun getPatchUpdates(refresh: Boolean = false): PatchUpdates
	suspend fun getPatchSizeInMb(): Double
	suspend fun clearPersistedData()
}

interface PatchFile {
	val installedVersion: Dao<Int>
	suspend fun getData(refresh: Boolean = false): PatchFileData
	suspend fun isUpdateAvailable(refresh: Boolean = false): Boolean
	suspend fun getSizeInMb(): Double
}