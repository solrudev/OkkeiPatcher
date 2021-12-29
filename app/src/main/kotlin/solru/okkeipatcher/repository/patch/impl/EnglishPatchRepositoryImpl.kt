package solru.okkeipatcher.repository.patch.impl

import solru.okkeipatcher.api.patchdata.EnglishPatchDataService
import solru.okkeipatcher.data.FileData
import solru.okkeipatcher.data.patchdata.EnglishPatchData
import solru.okkeipatcher.repository.patch.EnglishPatchRepository
import javax.inject.Inject

class EnglishPatchRepositoryImpl @Inject constructor(private val englishPatchDataService: EnglishPatchDataService) :
	EnglishPatchRepository {

	private var patchDataCache: EnglishPatchData? = null

	override suspend fun getScriptsData(): FileData {
		patchDataCache?.let { return it.scripts }
		return englishPatchDataService.getPatchData().scripts
	}

	override suspend fun getObbData(): FileData {
		patchDataCache?.let { return it.obb }
		return englishPatchDataService.getPatchData().obb
	}
}