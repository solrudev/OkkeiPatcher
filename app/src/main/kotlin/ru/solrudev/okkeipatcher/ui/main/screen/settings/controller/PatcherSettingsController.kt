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

import androidx.navigation.NavController
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsFragmentDirections
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.HandleSaveDataToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.SaveDataAccessRequestHandled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

class PatcherSettingsController(
	private val handleSaveData: SwitchPreferenceCompat?,
	clearData: Preference?,
	private val navController: NavController,
	private val viewModel: SettingsViewModel
) : JetView<SettingsUiState> {

	init {
		handleSaveData?.setOnPreferenceClickListener {
			viewModel.dispatchEvent(HandleSaveDataToggled)
			false
		}
		clearData?.setOnPreferenceClickListener {
			navigateToClearDataScreen()
			true
		}
	}

	override val trackedState = listOf(SettingsUiState::handleSaveData, SettingsUiState::requestSaveDataAccess)

	override fun render(uiState: SettingsUiState) {
		handleSaveData?.isChecked = uiState.handleSaveData
		if (uiState.requestSaveDataAccess) {
			navigateToSaveDataAccessScreen()
		}
	}

	private fun navigateToSaveDataAccessScreen() {
		viewModel.dispatchEvent(SaveDataAccessRequestHandled)
		val toSaveDataAccessScreen = SettingsFragmentDirections.actionSettingsFragmentToSaveDataAccessFragment()
		navController.navigateSafely(toSaveDataAccessScreen)
	}

	private fun navigateToClearDataScreen() {
		val toClearDataScreen = SettingsFragmentDirections.actionSettingsFragmentToClearDataFragment()
		navController.navigateSafely(toClearDataScreen)
	}
}