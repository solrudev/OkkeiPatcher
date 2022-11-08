package ru.solrudev.okkeipatcher.ui.navhost.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.domain.model.Work

sealed interface NavHostEvent : JetEvent {
	object NavigatedToWorkScreen : NavHostEvent
	object NavigatedToPermissionsScreen : NavHostEvent
	object PermissionsCheckRequested : NavHostEvent, NavHostEffect
	data class PermissionsChecked(val allPermissionsGranted: Boolean) : NavHostEvent
	data class WorkIsPending(val work: Work) : NavHostEvent
	data class UpdateAvailabilityChanged(val isUpdateAvailable: Boolean) : NavHostEvent
}

sealed interface NavHostEffect : JetEffect