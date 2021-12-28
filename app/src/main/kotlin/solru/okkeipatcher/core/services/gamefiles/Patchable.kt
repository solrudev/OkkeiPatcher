package solru.okkeipatcher.core.services.gamefiles

import solru.okkeipatcher.data.manifest.OkkeiManifest

interface Patchable {
	suspend fun patch(manifest: OkkeiManifest)
	suspend fun update(manifest: OkkeiManifest)
}