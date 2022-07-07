package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.PatchFileData
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates

interface PatchRepository {
	suspend fun getPatchUpdates(): PatchUpdates
	suspend fun getPatchSizeInMb(): Double
}

interface PatchFile {
	val installedVersion: Dao<Int>
	suspend fun getData(): PatchFileData
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getSizeInMb(): Double
}