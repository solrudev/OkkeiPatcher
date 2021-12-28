package solru.okkeipatcher.repository

import solru.okkeipatcher.data.manifest.OkkeiManifest

interface ManifestRepository {
	suspend fun getManifest(): OkkeiManifest
}