package ru.solrudev.okkeipatcher.ui.main.screen.settings.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent

sealed interface SettingsEvent : JetEvent {
	object HandleSaveDataClicked : SettingsEvent, SettingsEffect
	object SaveDataAccessRequested : SettingsEvent
	object SaveDataAccessRequestHandled : SettingsEvent
	data class HandleSaveDataChanged(val handleSaveData: Boolean) : SettingsEvent
}

sealed interface SettingsEffect : JetEffect