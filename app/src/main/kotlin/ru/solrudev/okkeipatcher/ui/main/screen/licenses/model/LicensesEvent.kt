package ru.solrudev.okkeipatcher.ui.main.screen.licenses.model

import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.License

sealed interface LicensesEvent : JetEvent {
	data class LicensesLoaded(val licenses: List<License>) : LicensesEvent
}