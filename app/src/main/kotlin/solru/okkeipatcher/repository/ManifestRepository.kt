package solru.okkeipatcher.repository

import solru.okkeipatcher.api.ManifestService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManifestRepository @Inject constructor(private val manifestService: ManifestService) {
	suspend fun getManifest() = manifestService.getManifest()
}