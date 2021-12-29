package solru.okkeipatcher.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import solru.okkeipatcher.data.FileData
import solru.okkeipatcher.data.OkkeiPatcherChangelog

interface OkkeiPatcherService {

	@GET("appdata") // TODO: replace with "app"
	suspend fun getOkkeiPatcherData(): FileData

	@GET("app/changelog")
	suspend fun getChangelog(
		@Query("locale") locale: String,
		@Header("Accept-Language") localeHeader: String = locale
	): OkkeiPatcherChangelog
}