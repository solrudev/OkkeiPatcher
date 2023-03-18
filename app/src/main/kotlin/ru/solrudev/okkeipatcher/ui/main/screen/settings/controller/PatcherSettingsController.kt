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