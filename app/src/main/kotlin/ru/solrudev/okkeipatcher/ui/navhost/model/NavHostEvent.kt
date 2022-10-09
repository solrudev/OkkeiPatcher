package ru.solrudev.okkeipatcher.ui.navhost.model

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface NavHostEvent : Event {
	object NavigatedToWorkScreen : NavHostEvent
	object NavigatedToPermissionsScreen : NavHostEvent
	object PermissionsCheckRequested : NavHostEvent, NavHostEffect
	data class PermissionsChecked(val allPermissionsGranted: Boolean) : NavHostEvent
	data class WorkIsPending(val work: Work) : NavHostEvent
}

sealed interface NavHostEffect : Effect