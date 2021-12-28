package solru.okkeipatcher.data.patchupdates

data class PatchUpdatesEnglish(val scripts: Boolean, val obb: Boolean) : PatchUpdates {

	override val apkUpdates: Boolean
		get() = scripts

	override val obbUpdates: Boolean
		get() = obb
}