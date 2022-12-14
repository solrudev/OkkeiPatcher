package ru.solrudev.okkeipatcher.ui.main.screen.home.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent

sealed interface HomeEvent : JetEvent {
	data class PatchStatusChanged(val patchStatus: PatchStatus) : HomeEvent
	data class PatchVersionChanged(val patchVersion: String) : HomeEvent
	object ViewHidden : HomeEvent
}

sealed interface PatchEvent : HomeEvent {
	data class PatchSizeLoaded(val patchSize: Double) : PatchEvent
	object PatchSizeLoadingStarted : PatchEvent
	object PatchRequested : PatchEvent, PatchEffect
	object StartPatch : PatchEvent, PatchEffect
	object StartPatchMessageShown : PatchEvent
	object StartPatchMessageDismissed : PatchEvent
}

sealed interface RestoreEvent : HomeEvent {
	object RestoreRequested : RestoreEvent
	object StartRestore : RestoreEvent, RestoreEffect
	object StartRestoreMessageShown : RestoreEvent
	object StartRestoreMessageDismissed : RestoreEvent
}

sealed interface PatchEffect : JetEffect
sealed interface RestoreEffect : JetEffect