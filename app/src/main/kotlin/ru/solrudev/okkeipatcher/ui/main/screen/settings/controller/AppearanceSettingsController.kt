package ru.solrudev.okkeipatcher.ui.main.screen.settings.controller

import androidx.preference.ListPreference
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.app.model.Theme
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.PersistTheme
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState

class AppearanceSettingsController(
	private val theme: ListPreference?,
	private val viewModel: SettingsViewModel
) : JetView<SettingsUiState> {

	init {
		theme?.setOnPreferenceChangeListener { preference, newValue ->
			preference as ListPreference
			val themeOrdinal = preference.findIndexOfValue(newValue as? String)
			viewModel.dispatchEvent(PersistTheme(Theme.fromOrdinal(themeOrdinal)))
		}
	}

	override val trackedState = listOf(SettingsUiState::theme)

	override fun render(uiState: SettingsUiState) {
		theme?.setValueIndex(uiState.theme.ordinal)
	}
}