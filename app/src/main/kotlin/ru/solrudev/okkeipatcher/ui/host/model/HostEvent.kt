package ru.solrudev.okkeipatcher.ui.host.model

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface HostEvent : Event {
	object NavigatedToWorkScreen : HostEvent
	object NavigatedToPermissionsScreen : HostEvent
	object PermissionsCheckRequested : HostEvent, HostEffect
	data class PermissionsChecked(val allPermissionsGranted: Boolean) : HostEvent
	data class WorkIsPending(val work: Work) : HostEvent
}

sealed interface HostEffect : Effect