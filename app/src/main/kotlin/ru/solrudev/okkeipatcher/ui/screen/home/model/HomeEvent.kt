package ru.solrudev.okkeipatcher.ui.screen.home.model

import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface HomeEvent : Event {
	data class PatchStatusChanged(val isPatched: Boolean) : HomeEvent
	data class PatchVersionChanged(val patchVersion: String) : HomeEvent
	object PatchUpdatesAvailable : HomeEvent
	object PatchUpdatesMessageShown : HomeEvent
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