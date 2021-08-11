package solru.okkeipatcher.model.dto.patchupdates

interface PatchUpdates {
	val apkUpdates: Boolean
	val obbUpdates: Boolean
	val available: Boolean
		get() = apkUpdates || obbUpdates
}