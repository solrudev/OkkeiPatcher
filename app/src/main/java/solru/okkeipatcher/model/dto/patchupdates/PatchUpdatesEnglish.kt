package solru.okkeipatcher.model.dto.patchupdates

import kotlinx.serialization.Serializable

@Serializable
data class PatchUpdatesEnglish(val scripts: Boolean, val obb: Boolean) : PatchUpdates {

	override val apkUpdates: Boolean
		get() = scripts

	override val obbUpdates: Boolean
		get() = obb
}