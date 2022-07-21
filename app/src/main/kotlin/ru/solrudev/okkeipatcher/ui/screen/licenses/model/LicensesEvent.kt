package ru.solrudev.okkeipatcher.ui.screen.licenses.model

import ru.solrudev.okkeipatcher.domain.model.License
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface LicensesEvent : Event {
	data class LicensesLoaded(val licenses: List<License>) : LicensesEvent
}