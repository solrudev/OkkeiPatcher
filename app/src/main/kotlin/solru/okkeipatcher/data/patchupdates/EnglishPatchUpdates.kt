package solru.okkeipatcher.data.patchupdates

data class EnglishPatchUpdates(val scriptsUpdateAvailable: Boolean, val obbUpdateAvailable: Boolean) : PatchUpdates {
	override val apkUpdatesAvailable: Boolean get() = scriptsUpdateAvailable
	override val obbUpdatesAvailable: Boolean get() = obbUpdateAvailable
}