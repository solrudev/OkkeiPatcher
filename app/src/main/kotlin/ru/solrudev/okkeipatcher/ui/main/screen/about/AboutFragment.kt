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

package ru.solrudev.okkeipatcher.ui.main.screen.about

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dev.chrisbanes.insetter.applyInsetter
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.versionCode
import ru.solrudev.okkeipatcher.data.util.versionName
import ru.solrudev.okkeipatcher.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

	private val binding by viewBinding(FragmentAboutBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		applyInsets()
		setupViews()
	}

	private fun setupViews() = with(binding) {
		val versionName = requireContext().versionName
		val versionCode = requireContext().versionCode
		textviewAboutVersion.text = getString(R.string.about_screen_version, "$versionName($versionCode)")
		buttonAboutSourceCode.setOnClickListener {
			val sourceCodeUri = Uri.parse(getString(R.string.about_screen_source_code_link))
			startActivity(Intent(ACTION_VIEW).setData(sourceCodeUri))
		}
	}

	private fun applyInsets() = with(binding) {
		containerAbout.applyInsetter {
			type(navigationBars = true) {
				padding()
			}
		}
	}
}