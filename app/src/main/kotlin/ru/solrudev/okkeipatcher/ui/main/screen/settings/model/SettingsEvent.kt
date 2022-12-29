package ru.solrudev.okkeipatcher.ui.main.screen.settings.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.domain.model.Theme

sealed interface SettingsEvent : JetEvent {
	object HandleSaveDataClicked : SettingsEvent, SettingsEffect
	object SaveDataAccessRequested : SettingsEvent
	object SaveDataAccessRequestHandled : SettingsEvent
	data class HandleSaveDataChanged(val handleSaveData: Boolean) : SettingsEvent
	data class ThemeChanged(val theme: Theme) : SettingsEvent
	data class PersistTheme(val theme: Theme) : SettingsEvent, SettingsEffect
}

sealed interface SettingsEffect : JetEffect