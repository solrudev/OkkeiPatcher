package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao

interface PatchFile {
	val installedVersion: Dao<Int>
	suspend fun getData(): FileDto
	suspend fun isUpdateAvailable(): Boolean
}

interface DefaultPatchRepository : PatchRepository {
	val scripts: PatchFile
	val obb: PatchFile
}