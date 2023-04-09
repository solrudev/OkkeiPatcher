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

import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.databinding.CardUpdateStatusBinding
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.util.localizedText

class UpdateStatusView(
	private val binding: CardUpdateStatusBinding
) : JetView<UpdateUiState> {

	override val trackedState = listOf(
		UpdateUiState::status,
		UpdateUiState::buttonText,
		UpdateUiState::isUpdateButtonVisible,
		UpdateUiState::isUpdateButtonEnabled,
		UpdateUiState::isUpdating
	)

	override fun render(uiState: UpdateUiState) = with(binding) {
		buttonCardUpdate.isVisible = uiState.isUpdateButtonVisible
		buttonCardUpdate.isEnabled = uiState.isUpdateButtonEnabled
		buttonCardUpdate.localizedText = uiState.buttonText
		buttonCardUpdate.setAbortEnabled(uiState.isUpdating, animate = false)
		textviewCardUpdateStatus.localizedText = uiState.status
	}
}