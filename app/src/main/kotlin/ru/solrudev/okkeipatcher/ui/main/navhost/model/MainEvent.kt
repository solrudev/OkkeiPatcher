package ru.solrudev.okkeipatcher.ui.main.navhost.model

import io.github.solrudev.jetmvi.JetEvent

sealed interface MainEvent : JetEvent {
	data class UpdateAvailabilityChanged(val isUpdateAvailable: Boolean) : MainEvent
}