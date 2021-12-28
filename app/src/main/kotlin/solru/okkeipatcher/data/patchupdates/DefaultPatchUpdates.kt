package solru.okkeipatcher.data.patchupdates

data class DefaultPatchUpdates(
	override val apkUpdates: Boolean = false,
	override val obbUpdates: Boolean = false
) : PatchUpdates