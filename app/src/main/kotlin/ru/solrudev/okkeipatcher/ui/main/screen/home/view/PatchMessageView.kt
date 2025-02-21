/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.lifecycle.Lifecycle
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.main.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.StartPatch
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.StartPatchMessageDismissed
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.StartPatchMessageShown
import ru.solrudev.okkeipatcher.ui.main.util.HapticFeedbackCallback
import ru.solrudev.okkeipatcher.ui.shared.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

class PatchMessageView(
	private val context: Context,
	private val viewLifecycle: Lifecycle,
	private val viewModel: HomeViewModel,
	private val hapticFeedbackCallback: HapticFeedbackCallback
) : JetView<HomeUiState> {

	override val trackedState = listOf(HomeUiState::startPatchMessage)

	override fun render(uiState: HomeUiState) {
		if (uiState.startPatchMessage.shouldShow) {
			showStartPatchMessage(uiState.startPatchMessage.data)
		}
	}

	private fun showStartPatchMessage(startPatchMessage: Message) {
		context.createDialogBuilder(startPatchMessage)
			.setIcon(R.drawable.ic_start_work)
			.setPositiveButton(R.string.button_text_start) { _, _ ->
				viewModel.dispatchEvent(StartPatch)
				hapticFeedbackCallback.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartPatchMessageDismissed)
			}
			.showWithLifecycle(viewLifecycle)
		viewModel.dispatchEvent(StartPatchMessageShown)
	}
}