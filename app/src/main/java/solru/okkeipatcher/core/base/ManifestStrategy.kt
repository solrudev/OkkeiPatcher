package solru.okkeipatcher.core.base

import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.manifest.OkkeiManifest

interface ManifestStrategy {
	fun patchUpdates(manifest: OkkeiManifest): PatchUpdates
	fun patchSizeInMb(manifest: OkkeiManifest): Double
}