package ru.solrudev.okkeipatcher.data.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto

interface OkkeiPatcherApi {

	@GET("appdata") // TODO: replace with "app"
	suspend fun getOkkeiPatcherData(): FileDto

	@GET("app/changelog")
	suspend fun getChangelog(
		@Query("locale") locale: String,
		@Header("Accept-Language") localeHeader: String = locale
	): OkkeiPatcherChangelogDto
}