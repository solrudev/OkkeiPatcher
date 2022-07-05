package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.PatchFileData

interface PatchFile {
	val installedVersion: Dao<Int>
	suspend fun getData(): PatchFileData
	suspend fun isUpdateAvailable(): Boolean
}

interface DefaultPatchRepository : PatchRepository {
	val scripts: PatchFile
	val obb: PatchFile
}