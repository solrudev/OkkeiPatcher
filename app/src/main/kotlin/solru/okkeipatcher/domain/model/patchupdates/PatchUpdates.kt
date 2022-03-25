package solru.okkeipatcher.domain.model.patchupdates

interface PatchUpdates {
	val apkUpdatesAvailable: Boolean
	val obbUpdatesAvailable: Boolean
	val available: Boolean get() = apkUpdatesAvailable || obbUpdatesAvailable
}