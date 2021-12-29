package solru.okkeipatcher.data.patchupdates

interface PatchUpdates {
	val apkUpdatesAvailable: Boolean
	val obbUpdatesAvailable: Boolean
	val available: Boolean get() = apkUpdatesAvailable || obbUpdatesAvailable
}