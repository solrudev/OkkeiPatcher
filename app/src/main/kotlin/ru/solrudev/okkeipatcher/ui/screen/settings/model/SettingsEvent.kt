package ru.solrudev.okkeipatcher.ui.screen.settings.model

import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface SettingsEvent : Event {
	object HandleSaveDataClicked : SettingsEvent, SettingsEffect
	object SaveDataAccessRequested : SettingsEvent
	object SaveDataAccessRequestHandled : SettingsEvent
	data class HandleSaveDataChanged(val handleSaveData: Boolean) : SettingsEvent
}

sealed interface SettingsEffect : Effect