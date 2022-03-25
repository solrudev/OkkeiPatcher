package solru.okkeipatcher.ui.fragment

import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.R
import solru.okkeipatcher.ui.viewmodel.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

	private val settingsViewModel: SettingsViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		super.onPrepareOptionsMenu(menu)
		menu.removeItem(R.id.settings_fragment)
	}

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.okkei_preferences, rootKey)
		findPreference<Preference>("clear_data")?.setOnPreferenceClickListener {
			// TODO
			true
		}
		findPreference<Preference>("about")?.setOnPreferenceClickListener {
			val toAboutFragment = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
			findNavController().navigate(toAboutFragment)
			true
		}
		findPreference<Preference>("third_party_notices")?.setOnPreferenceClickListener {
			// TODO
			true
		}
	}
}