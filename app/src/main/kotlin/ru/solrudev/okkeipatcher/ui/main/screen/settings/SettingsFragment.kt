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

package ru.solrudev.okkeipatcher.ui.main.screen.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.HostJetView
import io.github.solrudev.jetmvi.derivedView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.ui.main.screen.settings.controller.AppearanceSettingsController
import ru.solrudev.okkeipatcher.ui.main.screen.settings.controller.MiscellaneousSettingsController
import ru.solrudev.okkeipatcher.ui.main.screen.settings.controller.PatcherSettingsController
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState

private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), HostJetView<SettingsUiState> {

	private val handleSaveData: SwitchPreferenceCompat?
		get() = findPreference(getString(R.string.preference_key_handle_save_data))

	private val clearData: Preference?
		get() = findPreference(getString(R.string.preference_key_clear_data))

	private val theme: ListPreference?
		get() = findPreference(getString(R.string.preference_key_theme))

	private val about: Preference?
		get() = findPreference(getString(R.string.preference_key_about))

	private val thirdPartyLicenses: Preference?
		get() = findPreference(getString(R.string.preference_key_licenses))

	private val patcherSettingsController by derivedView {
		PatcherSettingsController(handleSaveData, clearData, findNavController(), viewModel)
	}

	private val appearanceSettingsController by derivedView {
		AppearanceSettingsController(theme, viewModel)
	}

	private val miscellaneousSettingsController by derivedView {
		MiscellaneousSettingsController(about, thirdPartyLicenses, findNavController())
	}

	private val viewModel: SettingsViewModel by jetViewModels(
		SettingsFragment::patcherSettingsController,
		SettingsFragment::appearanceSettingsController,
		SettingsFragment::miscellaneousSettingsController
	)

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.okkei_preferences, rootKey)
	}

	override fun onDisplayPreferenceDialog(preference: Preference) {
		if (parentFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
			return
		}
		if (preference is ListPreference) {
			val dialogFragment = MaterialListPreferenceDialogFragment.newInstance(preference.key)
			@Suppress("DEPRECATION")
			dialogFragment.setTargetFragment(this, 0)
			dialogFragment.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
			return
		}
		super.onDisplayPreferenceDialog(preference)
	}
}