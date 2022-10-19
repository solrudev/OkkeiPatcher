package ru.solrudev.okkeipatcher.ui.screen.update.model

import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface UpdateEvent : Event {
	object UpdateDataLoadingStarted : UpdateEvent
	data class UpdateDataRequested(val refresh: Boolean) : UpdateEvent, UpdateEffect
	data class UpdateDataLoaded(val updateData: OkkeiPatcherUpdateData) : UpdateEvent
}

sealed interface UpdateEffect : Effect