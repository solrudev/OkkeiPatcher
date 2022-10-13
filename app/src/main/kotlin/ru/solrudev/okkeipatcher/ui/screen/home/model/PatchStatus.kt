package ru.solrudev.okkeipatcher.ui.screen.home.model

sealed interface PatchStatus {
	object Patched : PatchStatus, PersistentPatchStatus
	object NotPatched : PatchStatus, PersistentPatchStatus
	object UpdateAvailable : PatchStatus
	data class WorkStarted(val currentStatus: PersistentPatchStatus) : PatchStatus
}

sealed interface PersistentPatchStatus : PatchStatus