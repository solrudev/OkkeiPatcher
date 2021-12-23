package solru.okkeipatcher.core.services.files

import solru.okkeipatcher.model.manifest.OkkeiManifest

interface Patchable {
	suspend fun patch(manifest: OkkeiManifest)
	suspend fun update(manifest: OkkeiManifest)
}