/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
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
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState

class AvailableOperationModesController(
	private val operationMode: ListPreference?,
) : JetView<SettingsUiState> {

	override val trackedState = listOf(SettingsUiState::availableOperationModes)

	override fun render(uiState: SettingsUiState) {
		operationMode?.apply {
			entries = uiState.availableOperationModes
				.map { it.title.resolve(context) }
				.toTypedArray()
			entryValues = uiState.availableOperationModes
				.map { it.value }
				.toTypedArray()
		}
	}
}