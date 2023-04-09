/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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