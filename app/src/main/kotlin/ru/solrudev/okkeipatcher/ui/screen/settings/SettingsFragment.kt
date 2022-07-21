package ru.solrudev.okkeipatcher.ui.screen.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.renderBy
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent.HandleSaveDataClicked
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent.SaveDataAccessRequestHandled
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.util.prepareOptionsMenu
import ru.solrudev.okkeipatcher.ui.util.setupTransitions

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), FeatureView<SettingsUiState> {

	private val viewModel by viewModels<SettingsViewModel>()

	private val handleSaveData: SwitchPreferenceCompat?
		get() = findPreference("handle_save_data")

	private val clearData: Preference?
		get() = findPreference("clear_data")

	private val about: Preference?
		get() = findPreference("about")

	private val thirdPartyLicenses: Preference?
		get() = findPreference("third_party_licenses")

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		prepareOptionsMenu {
			removeItem(R.id.settings_fragment)
		}
		viewModel.renderBy(this)
		// Workaround for a bug in transition
		val colorBackground = MaterialColors.getColor(view, android.R.attr.colorBackground)
		view.setBackgroundColor(colorBackground)
	}

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
		findNavController().navigate(toSaveDataAccessScreen)
		viewModel.dispatchEvent(SaveDataAccessRequestHandled)
	}

	private fun navigateToClearDataScreen() {
		val toClearDataScreen = SettingsFragmentDirections.actionSettingsFragmentToClearDataFragment()
		findNavController().navigate(toClearDataScreen)
	}

	private fun navigateToAboutScreen() {
		val toAboutScreen = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
		findNavController().navigate(toAboutScreen)
	}

	private fun navigateToLicensesScreen() {
		val toLicensesScreen = SettingsFragmentDirections.actionSettingsFragmentToLicensesFragment()
		findNavController().navigate(toLicensesScreen)
	}
}