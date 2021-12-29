package solru.okkeipatcher.repository.patch

import solru.okkeipatcher.data.FileData

interface ObbDataRepository {
	suspend fun getObbData(): FileData
}