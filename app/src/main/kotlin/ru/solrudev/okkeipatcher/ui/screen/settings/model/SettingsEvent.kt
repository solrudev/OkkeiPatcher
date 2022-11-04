package ru.solrudev.okkeipatcher.ui.screen.settings.model

import io.github.solrudev.jetmvi.Effect
import io.github.solrudev.jetmvi.Event

sealed interface SettingsEvent : Event {
	object HandleSaveDataClicked : SettingsEvent, SettingsEffect
	object SaveDataAccessRequested : SettingsEvent
	object SaveDataAccessRequestHandled : SettingsEvent
	data class HandleSaveDataChanged(val handleSaveData: Boolean) : SettingsEvent
}

sealed interface SettingsEffect : Effect