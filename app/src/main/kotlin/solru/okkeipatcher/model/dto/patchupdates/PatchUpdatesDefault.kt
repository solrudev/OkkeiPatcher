package solru.okkeipatcher.model.dto.patchupdates

data class PatchUpdatesDefault(
	override val apkUpdates: Boolean = false,
	override val obbUpdates: Boolean = false
) : PatchUpdates