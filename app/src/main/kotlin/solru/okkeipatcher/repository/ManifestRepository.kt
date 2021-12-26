package solru.okkeipatcher.repository

import solru.okkeipatcher.model.manifest.OkkeiManifest

interface ManifestRepository {
	suspend fun getManifest(): OkkeiManifest
}