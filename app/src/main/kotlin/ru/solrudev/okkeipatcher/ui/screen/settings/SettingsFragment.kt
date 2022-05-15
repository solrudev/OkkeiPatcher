package ru.solrudev.okkeipatcher.ui.screen.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.ui.util.extension.prepareOptionsMenu
import ru.solrudev.okkeipatcher.ui.util.extension.setupTransitions

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

	private val viewModel by viewModels<SettingsViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		prepareOptionsMenu {
			removeItem(R.id.settings_fragment)
		}
		// Workaround for a bug in transition
		val colorBackground = MaterialColors.getColor(view, android.R.attr.colorBackground)
		view.setBackgroundColor(colorBackground)
	}

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.okkei_preferences, rootKey)
		findPreference<Preference>("clear_data")?.setOnPreferenceClickListener {
			// TODO
			true
		}
		findPreference<Preference>("about")?.setOnPreferenceClickListener {
			val toAboutScreen = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
			findNavController().navigate(toAboutScreen)
			true
		}
		findPreference<Preference>("third_party_notices")?.setOnPreferenceClickListener {
			// TODO
			true
		}
	}
}