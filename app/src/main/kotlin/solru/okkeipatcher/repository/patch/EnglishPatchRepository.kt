package solru.okkeipatcher.repository.patch

import solru.okkeipatcher.data.FileData

interface EnglishPatchRepository {
	suspend fun getScriptsData(): FileData
	suspend fun getObbData(): FileData
}