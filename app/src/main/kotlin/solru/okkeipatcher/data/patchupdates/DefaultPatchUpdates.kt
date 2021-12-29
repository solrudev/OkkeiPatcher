package solru.okkeipatcher.data.patchupdates

data class DefaultPatchUpdates(
	val scriptsUpdateAvailable: Boolean = false,
	val obbUpdateAvailable: Boolean = false
) : PatchUpdates {
	override val apkUpdatesAvailable: Boolean get() = scriptsUpdateAvailable
	override val obbUpdatesAvailable: Boolean get() = obbUpdateAvailable
}