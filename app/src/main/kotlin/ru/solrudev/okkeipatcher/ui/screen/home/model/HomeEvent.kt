package ru.solrudev.okkeipatcher.ui.screen.home.model

import io.github.solrudev.jetmvi.Effect
import io.github.solrudev.jetmvi.Event

sealed interface HomeEvent : Event {
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

sealed interface PatchEffect : Effect
sealed interface RestoreEffect : Effect