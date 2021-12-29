package solru.okkeipatcher.api.patchdata

import retrofit2.http.GET
import solru.okkeipatcher.data.patchdata.DefaultPatchData

interface DefaultPatchDataService {

	@GET("patch/en")
	suspend fun getPatchData(): DefaultPatchData
}