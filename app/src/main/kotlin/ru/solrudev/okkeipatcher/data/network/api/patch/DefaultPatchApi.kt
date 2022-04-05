package ru.solrudev.okkeipatcher.data.network.api.patch

import retrofit2.http.GET
import ru.solrudev.okkeipatcher.data.network.model.patch.DefaultPatchDto

interface DefaultPatchApi {

	@GET("patch/en")
	suspend fun getPatchData(): DefaultPatchDto
}