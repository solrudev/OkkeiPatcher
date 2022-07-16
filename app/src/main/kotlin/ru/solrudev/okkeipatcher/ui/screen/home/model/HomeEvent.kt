package ru.solrudev.okkeipatcher.ui.screen.home.model

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface HomeEvent : Event {
	data class WorkIsPending(val work: Work) : HomeEvent
	data class WorkFinished(val success: Boolean) : HomeEvent, HomeEffect
	data class PatchStatusChanged(val isPatched: Boolean) : HomeEvent
	object PatchUpdatesAvailable : HomeEvent
	object PatchUpdatesMessageShown : HomeEvent
	object NavigatedToWorkScreen : HomeEvent
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

sealed interface HomeEffect : Effect
sealed interface PatchEffect : Effect
sealed interface RestoreEffect : Effect