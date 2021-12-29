package solru.okkeipatcher.core.strategy

import solru.okkeipatcher.data.patchupdates.PatchUpdates

interface PatchDataStrategy {
	suspend fun getPatchUpdates(): PatchUpdates
	suspend fun getPatchSizeInMb(): Double
}