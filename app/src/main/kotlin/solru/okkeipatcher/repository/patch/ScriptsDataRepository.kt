package solru.okkeipatcher.repository.patch

import solru.okkeipatcher.data.FileData

interface ScriptsDataRepository {
	suspend fun getScriptsData(): FileData
}