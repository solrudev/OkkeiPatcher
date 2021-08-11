package solru.okkeipatcher.model.dto

import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdatesDefault

data class AppServiceConfig(
	val processSaveData: Boolean,
	val patchUpdates: PatchUpdates = PatchUpdatesDefault()
)