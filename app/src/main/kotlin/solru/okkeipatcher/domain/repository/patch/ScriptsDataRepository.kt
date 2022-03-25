package solru.okkeipatcher.domain.repository.patch

import solru.okkeipatcher.data.network.model.FileDto

interface ScriptsDataRepository {
	suspend fun getScriptsData(): FileDto
}