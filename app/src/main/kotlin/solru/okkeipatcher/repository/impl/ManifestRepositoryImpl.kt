package solru.okkeipatcher.repository.impl

import solru.okkeipatcher.api.ManifestService
import solru.okkeipatcher.repository.ManifestRepository
import javax.inject.Inject

class ManifestRepositoryImpl @Inject constructor(private val manifestService: ManifestService) : ManifestRepository {
	override suspend fun getManifest() = manifestService.getManifest()
}