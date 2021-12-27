package solru.okkeipatcher.model.dto

import solru.okkeipatcher.model.dto.patchupdates.DefaultPatchUpdates
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates

data class ServiceConfig(
	val processSaveData: Boolean,
	val patchUpdates: PatchUpdates = DefaultPatchUpdates()
)