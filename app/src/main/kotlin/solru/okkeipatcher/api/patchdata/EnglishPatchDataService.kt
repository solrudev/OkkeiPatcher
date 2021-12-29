package solru.okkeipatcher.api.patchdata

import retrofit2.http.GET
import solru.okkeipatcher.data.patchdata.EnglishPatchData

interface EnglishPatchDataService {

	@GET("patch/en")
	suspend fun getPatchData(): EnglishPatchData
}