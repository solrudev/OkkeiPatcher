/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

import android.view.inputmethod.EditorInfo
import androidx.preference.EditTextPreference
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.PatchApiUrlChanged
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState

class PatchApiUrlController(
	private val patchApiUrl: EditTextPreference?,
	private val viewModel: SettingsViewModel
) : JetView<SettingsUiState> {

	init {
		patchApiUrl?.setOnBindEditTextListener { editText ->
			editText.inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_URI
		}
		patchApiUrl?.setOnPreferenceChangeListener { _, newValue ->
			viewModel.dispatchEvent(PatchApiUrlChanged(newValue as String))
			true
		}
	}

	override val trackedState = listOf(SettingsUiState::patchApiUrl)

	override fun render(uiState: SettingsUiState) {
		patchApiUrl?.text = uiState.patchApiUrl
	}
}