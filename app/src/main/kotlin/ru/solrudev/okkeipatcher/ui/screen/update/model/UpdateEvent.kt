package ru.solrudev.okkeipatcher.ui.screen.update.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData

sealed interface UpdateEvent : JetEvent {
	object UpdateDataLoadingStarted : UpdateEvent
	data class UpdateDataRequested(val refresh: Boolean) : UpdateEvent, UpdateEffect
	data class UpdateDataLoaded(val updateData: OkkeiPatcherUpdateData) : UpdateEvent
}

sealed interface UpdateEffect : JetEffect