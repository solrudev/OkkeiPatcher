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

package ru.solrudev.okkeipatcher.ui.main.screen.update.view

import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.versionName
import ru.solrudev.okkeipatcher.databinding.CardUpdateAppInfoBinding
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.percentDone

class AppInfoView(
	private val binding: CardUpdateAppInfoBinding
) : JetView<UpdateUiState> {

	private val context by binding.root::context

	override val trackedState = listOf(
		UpdateUiState::isUpdating,
		UpdateUiState::isDownloading,
		UpdateUiState::isInstalling,
		UpdateUiState::percentDone,
		UpdateUiState::progressData,
		UpdateUiState::updateSize
	)

	override fun render(uiState: UpdateUiState) = with(binding) {
		textviewCardAppSubtitle.text = subtitleText(uiState)
		setProgress(uiState)
	}

	private fun setProgress(uiState: UpdateUiState) = with(binding.progressiconCardAppIcon) {
		setProgressVisible(uiState.isUpdating, animate = true)
		setProgressCompat(uiState.progressData.progress, animated = true)
		max = uiState.progressData.max
		isIndeterminate = uiState.isInstalling
	}

	private fun subtitleText(uiState: UpdateUiState): String {
		val text = if (uiState.isDownloading) {
			context.getString(R.string.update_percent_done, uiState.percentDone, uiState.updateSize)
		} else {
			context.getString(R.string.card_app_version, context.versionName)
		}
		return text
	}
}