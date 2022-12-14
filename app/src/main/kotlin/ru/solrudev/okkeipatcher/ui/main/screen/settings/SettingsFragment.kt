package ru.solrudev.okkeipatcher.ui.main.screen.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.HandleSaveDataClicked
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.SaveDataAccessRequestHandled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), JetView<SettingsUiState> {

	private val viewModel: SettingsViewModel by jetViewModels()

	private val handleSaveData: SwitchPreferenceCompat?
		get() = findPreference(getString(R.string.preference_key_handle_save_data))

	private val clearData: Preference?
		get() = findPreference(getString(R.string.preference_key_clear_data))

	private val about: Preference?
		get() = findPreference(getString(R.string.preference_key_about))

	private val thirdPartyLicenses: Preference?
		get() = findPreference(getString(R.string.preference_key_licenses))

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.okkei_preferences, rootKey)
		setupClickListeners()
	}

	override fun render(uiState: SettingsUiState) {
		handleSaveData?.isChecked = uiState.handleSaveData
		if (uiState.requestSaveDataAccess) {
			navigateToSaveDataAccessScreen()
		}
	}

	private fun setupClickListeners() {
		handleSaveData?.setOnPreferenceClickListener {
			viewModel.dispatchEvent(HandleSaveDataClicked)
			false
		}
		clearData?.setOnPreferenceClickListener {
			navigateToClearDataScreen()
			true
		}
		about?.setOnPreferenceClickListener {
			navigateToAboutScreen()
			true
		}
		thirdPartyLicenses?.setOnPreferenceClickListener {
			navigateToLicensesScreen()
			true
		}
	}

	private fun navigateToSaveDataAccessScreen() {
		val toSaveDataAccessScreen = SettingsFragmentDirections.actionSettingsFragmentToSaveDataAccessFragment()
		findNavController().navigateSafely(toSaveDataAccessScreen)
		viewModel.dispatchEvent(SaveDataAccessRequestHandled)
	}

	private fun navigateToClearDataScreen() {
		val toClearDataScreen = SettingsFragmentDirections.actionSettingsFragmentToClearDataFragment()
		findNavController().navigateSafely(toClearDataScreen)
	}

	private fun navigateToAboutScreen() {
		val toAboutScreen = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
		findNavController().navigateSafely(toAboutScreen)
	}

	private fun navigateToLicensesScreen() {
		val toLicensesScreen = SettingsFragmentDirections.actionSettingsFragmentToLicensesFragment()
		findNavController().navigateSafely(toLicensesScreen)
	}
}