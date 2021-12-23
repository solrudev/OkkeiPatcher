package solru.okkeipatcher.core.strategy

import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.manifest.OkkeiManifest

interface PatchInfoStrategy {
	fun patchUpdates(manifest: OkkeiManifest): PatchUpdates
	fun patchSizeInMb(manifest: OkkeiManifest): Double
}