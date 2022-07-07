package ru.solrudev.okkeipatcher.ui.screen.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.databinding.FragmentAboutBinding
import ru.solrudev.okkeipatcher.ui.util.prepareOptionsMenu
import ru.solrudev.okkeipatcher.ui.util.setupTransitions

class AboutFragment : Fragment(R.layout.fragment_about) {

	private val binding by viewBinding(FragmentAboutBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		prepareOptionsMenu {
			removeItem(R.id.settings_fragment)
		}
		val versionName = requireContext().run {
			packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA).versionName
		}
		val versionCode = requireContext().versionCode
		binding.textviewAboutVersion.text = getString(R.string.about_version, "$versionName($versionCode)")
	}
}