package solru.okkeipatcher.data

import solru.okkeipatcher.data.patchupdates.DefaultPatchUpdates
import solru.okkeipatcher.data.patchupdates.PatchUpdates

data class ServiceConfig(
	val processSaveData: Boolean,
	val patchUpdates: PatchUpdates = DefaultPatchUpdates()
)