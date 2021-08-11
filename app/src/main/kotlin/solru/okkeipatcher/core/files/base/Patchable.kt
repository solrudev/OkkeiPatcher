package solru.okkeipatcher.core.files.base

import solru.okkeipatcher.model.manifest.OkkeiManifest

interface Patchable {
	suspend fun patch(manifest: OkkeiManifest)
	suspend fun update(manifest: OkkeiManifest)
}