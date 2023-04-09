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

package ru.solrudev.okkeipatcher.ui.main.navhost.view

import android.graphics.Color
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentMainBinding
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import ru.solrudev.okkeipatcher.ui.main.util.getMaterialColor

class UpdateBadgeView(private val binding: FragmentMainBinding) : JetView<MainUiState> {

	private val context by binding.root::context

	override fun render(uiState: MainUiState) {
		displayUpdateBadge(uiState.isUpdateAvailable)
	}

	private fun displayUpdateBadge(isUpdateAvailable: Boolean) = with(binding) {
		if (isUpdateAvailable) {
			val color = context.getMaterialColor(com.google.android.material.R.attr.colorError, Color.RED)
			bottomNavigationViewMain?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
			navigationRailViewMain?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
		} else {
			bottomNavigationViewMain?.removeBadge(R.id.update_fragment)
			navigationRailViewMain?.removeBadge(R.id.update_fragment)
		}
	}
}