/*
 * Okkei Patcher
 * Copyright (C) 2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.ui.main.screen.home.view

import android.content.Context
import android.widget.TextView
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState

class PatchVersionView(
	private val context: Context,
	private val textView: TextView
) : JetView<HomeUiState> {

	override val trackedState = listOf(HomeUiState::patchVersion)

	override fun render(uiState: HomeUiState) {
		val version = uiState.patchVersion.ifEmpty { context.getString(R.string.not_available) }
		textView.text = context.getString(R.string.card_patch_version, version)
	}
}