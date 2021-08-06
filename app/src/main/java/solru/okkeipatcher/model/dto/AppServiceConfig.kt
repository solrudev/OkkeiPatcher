package solru.okkeipatcher.model.dto

import kotlinx.serialization.Serializable
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates

@Serializable
data class AppServiceConfig(
	val processSaveData: Boolean,
	val patchUpdates: PatchUpdates
)