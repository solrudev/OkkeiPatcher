package solru.okkeipatcher.api

import retrofit2.http.GET
import solru.okkeipatcher.data.manifest.OkkeiManifest

interface ManifestService {

	@GET("Manifest.json")
	suspend fun getManifest(): OkkeiManifest
}