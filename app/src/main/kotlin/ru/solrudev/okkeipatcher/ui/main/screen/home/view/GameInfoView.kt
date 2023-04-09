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

package ru.solrudev.okkeipatcher.ui.main.screen.home.view

import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.CardGameInfoBinding
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.GameUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState

class GameInfoView(private val binding: CardGameInfoBinding) : JetView<HomeUiState> {

	private val context by binding.root::context

	init {
		loadGameInfo()
	}

	override val trackedState = listOf(HomeUiState::patchVersion)

	override fun render(uiState: HomeUiState) = with(binding) {
		val version = uiState.patchVersion.ifEmpty { context.getString(R.string.not_available) }
		textviewCardGamePatch.text = context.getString(R.string.card_patch_version, version)
	}

	private fun loadGameInfo() = with(binding) {
		val gameUiState = GameUiState(context)
		textviewCardGameTitle.text = gameUiState.title
		textviewCardGameVersion.text = context.getString(R.string.card_app_version, gameUiState.version)
		imageviewCardGameIcon.setImageDrawable(gameUiState.icon)
	}
}