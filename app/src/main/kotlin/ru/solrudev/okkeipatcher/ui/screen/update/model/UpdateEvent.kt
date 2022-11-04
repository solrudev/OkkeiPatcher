package ru.solrudev.okkeipatcher.ui.screen.update.model

import io.github.solrudev.jetmvi.Effect
import io.github.solrudev.jetmvi.Event
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData

sealed interface UpdateEvent : Event {
	object UpdateDataLoadingStarted : UpdateEvent
	data class UpdateDataRequested(val refresh: Boolean) : UpdateEvent, UpdateEffect
	data class UpdateDataLoaded(val updateData: OkkeiPatcherUpdateData) : UpdateEvent
}

sealed interface UpdateEffect : Effect