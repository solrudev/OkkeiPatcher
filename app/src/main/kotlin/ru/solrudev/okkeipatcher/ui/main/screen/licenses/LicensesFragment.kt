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

package ru.solrudev.okkeipatcher.ui.main.screen.licenses

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.bind
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentLicensesBinding
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesUiState

@AndroidEntryPoint
class LicensesFragment : Fragment(R.layout.fragment_licenses), JetView<LicensesUiState> {

	private val binding by viewBinding(FragmentLicensesBinding::bind)
	private val viewModel: LicensesViewModel by viewModels()
	private val licensesAdapter = LicensesAdapter()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		applyInsets()
		binding.recyclerviewLicenses.adapter = licensesAdapter
		viewModel.bind(this)
	}

	override fun render(uiState: LicensesUiState) {
		licensesAdapter.submitList(uiState.licenses)
	}

	private fun applyInsets() = with(binding) {
		recyclerviewLicenses.applyInsetter {
			type(navigationBars = true) {
				padding()
			}
		}
	}
}