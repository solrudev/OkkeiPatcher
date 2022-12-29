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