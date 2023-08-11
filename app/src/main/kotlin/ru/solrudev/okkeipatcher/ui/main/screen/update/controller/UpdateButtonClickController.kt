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

package ru.solrudev.okkeipatcher.ui.main.screen.update.controller

import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.ui.main.screen.update.UpdateViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.CancelWork
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDownloadRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateInstallRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateState
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.widget.AbortButton

class UpdateButtonClickController(
	private val button: AbortButton,
	private val viewModel: UpdateViewModel
) : JetView<UpdateUiState> {

	private var work: Work? = null

	override val trackedState = listOf(
		UpdateUiState::currentWork,
		UpdateUiState::state
	)

	override fun render(uiState: UpdateUiState) {
		work = uiState.currentWork
		setButtonOnClickListener(uiState)
	}

	private fun setButtonOnClickListener(uiState: UpdateUiState) = with(button) {
		when (uiState.state) {
			is UpdateState.Idle -> setOnClickListener {
				viewModel.dispatchEvent(UpdateDownloadRequested)
			}
			is UpdateState.InstallPending -> setOnClickListener {
				viewModel.dispatchEvent(UpdateInstallRequested)
			}
			is UpdateState.Updating -> setOnClickListener {
				work?.let { work ->
					viewModel.dispatchEvent(CancelWork(work))
				}
			}
		}
	}
}