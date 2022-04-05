package ru.solrudev.okkeipatcher.ui.fragment

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentAboutBinding
import ru.solrudev.okkeipatcher.util.appVersionCode
import ru.solrudev.okkeipatcher.util.appVersionString

class AboutFragment : Fragment(R.layout.fragment_about) {

	private val binding by viewBinding(FragmentAboutBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		super.onPrepareOptionsMenu(menu)
		menu.removeItem(R.id.settings_fragment)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.textviewAboutVersion.text = getString(R.string.about_version, "$appVersionString($appVersionCode)")
	}
}