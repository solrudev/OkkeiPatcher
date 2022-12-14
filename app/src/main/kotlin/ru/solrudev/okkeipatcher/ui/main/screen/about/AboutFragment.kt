package ru.solrudev.okkeipatcher.ui.main.screen.about

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.data.util.versionName
import ru.solrudev.okkeipatcher.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

	private val binding by viewBinding(FragmentAboutBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		val versionName = requireContext().versionName
		val versionCode = requireContext().versionCode
		textviewAboutVersion.text = getString(R.string.about_screen_version, "$versionName($versionCode)")
		buttonAboutSourceCode.setOnClickListener {
			val sourceCodeUri = Uri.parse(getString(R.string.about_screen_source_code_link))
			startActivity(Intent(ACTION_VIEW).setData(sourceCodeUri))
		}
	}
}