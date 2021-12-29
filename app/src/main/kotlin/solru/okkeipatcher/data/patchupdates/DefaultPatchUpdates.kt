package solru.okkeipatcher.data.patchupdates

data class DefaultPatchUpdates(
	override val apkUpdatesAvailable: Boolean = false,
	override val obbUpdatesAvailable: Boolean = false
) : PatchUpdates