package ru.solrudev.okkeipatcher.ui.screen.licenses.model

import io.github.solrudev.jetmvi.Event
import ru.solrudev.okkeipatcher.domain.model.License

sealed interface LicensesEvent : Event {
	data class LicensesLoaded(val licenses: List<License>) : LicensesEvent
}