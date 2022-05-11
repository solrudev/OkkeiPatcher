package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.data.network.model.FileDto

interface ScriptsDataRepository {
	suspend fun getScriptsData(): FileDto
}

interface ObbDataRepository {
	suspend fun getObbData(): FileDto
}

interface DefaultPatchRepository : PatchRepository, ScriptsDataRepository, ObbDataRepository