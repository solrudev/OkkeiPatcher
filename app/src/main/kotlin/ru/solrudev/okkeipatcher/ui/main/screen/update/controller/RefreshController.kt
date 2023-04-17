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

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.update.UpdateViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState

class RefreshController(
	private val swipeRefreshLayout: SwipeRefreshLayout,
	private val viewModel: UpdateViewModel
) : JetView<UpdateUiState> {

	init {
		setupRefresh()
	}

	override val trackedState = listOf(UpdateUiState::isLoading)

	override fun render(uiState: UpdateUiState) {
		swipeRefreshLayout.isRefreshing = uiState.isLoading
	}

	private fun setupRefresh() = with(swipeRefreshLayout) {
		setDistanceToTriggerSync((192 * context.resources.displayMetrics.density).toInt())
		setOnRefreshListener {
			viewModel.dispatchEvent(UpdateDataRequested(refresh = true))
		}
	}
}