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
import io.github.solrudev.jetmvi.HostJetView
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsFragmentDirections
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

class MiscellaneousSettingsController(
	about: Preference?,
	thirdPartyLicenses: Preference?,
	private val navController: NavController
) : HostJetView<SettingsUiState> {

	init {
		about?.setOnPreferenceClickListener {
			navigateToAboutScreen()
			true
		}
		thirdPartyLicenses?.setOnPreferenceClickListener {
			navigateToLicensesScreen()
			true
		}
	}

	private fun navigateToAboutScreen() {
		val toAboutScreen = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
		navController.navigateSafely(toAboutScreen)
	}

	private fun navigateToLicensesScreen() {
		val toLicensesScreen = SettingsFragmentDirections.actionSettingsFragmentToLicensesFragment()
		navController.navigateSafely(toLicensesScreen)
	}
}