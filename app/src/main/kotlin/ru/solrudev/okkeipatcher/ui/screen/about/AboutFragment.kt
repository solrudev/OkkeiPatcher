package ru.solrudev.okkeipatcher.ui.screen.about

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

	private val binding by viewBinding(FragmentAboutBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val versionName = requireContext().run {
			@Suppress("DEPRECATION")
			packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA).versionName
		}
		val versionCode = requireContext().versionCode
		binding.textviewAboutVersion.text = getString(R.string.about_version, "$versionName($versionCode)")
		binding.buttonAboutSourceCode.setOnClickListener {
			val sourceCodeUri = Uri.parse(getString(R.string.source_code_link))
			startActivity(Intent(ACTION_VIEW).setData(sourceCodeUri))
		}
	}
}