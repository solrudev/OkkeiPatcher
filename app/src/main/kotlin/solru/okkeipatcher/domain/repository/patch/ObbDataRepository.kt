package solru.okkeipatcher.domain.repository.patch

import solru.okkeipatcher.data.network.model.FileDto

interface ObbDataRepository {
	suspend fun getObbData(): FileDto
}