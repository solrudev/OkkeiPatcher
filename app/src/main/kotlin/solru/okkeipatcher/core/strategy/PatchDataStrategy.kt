package solru.okkeipatcher.core.strategy

import solru.okkeipatcher.data.manifest.OkkeiManifest
import solru.okkeipatcher.data.patchupdates.PatchUpdates

interface PatchDataStrategy {
	fun patchUpdates(manifest: OkkeiManifest): PatchUpdates
	fun patchSizeInMb(manifest: OkkeiManifest): Double
}