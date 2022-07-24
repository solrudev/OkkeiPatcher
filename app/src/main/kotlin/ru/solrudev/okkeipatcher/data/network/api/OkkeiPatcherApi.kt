package ru.solrudev.okkeipatcher.data.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherVersionDto

interface OkkeiPatcherApi {

	@GET("app")
	suspend fun getOkkeiPatcherData(): FileDto

	@GET("app/changelog")
	suspend fun getChangelog(
		@Query("version") currentVersion: Int,
		@Header("Accept-Language") language: String
	): List<OkkeiPatcherVersionDto>
}