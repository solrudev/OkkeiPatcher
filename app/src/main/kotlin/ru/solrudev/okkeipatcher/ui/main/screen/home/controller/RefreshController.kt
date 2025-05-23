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

package ru.solrudev.okkeipatcher.ui.main.screen.home.controller

import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.RefreshRequested
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.util.HapticFeedbackCallback
import ru.solrudev.okkeipatcher.ui.main.util.applyDistanceToTrigger

class RefreshController(
	private val swipeRefreshLayout: SwipeRefreshLayout,
	private val viewModel: HomeViewModel,
	private val hapticFeedbackCallback: HapticFeedbackCallback
) : JetView<HomeUiState> {

	init {
		setupRefresh()
	}

	override val trackedState = listOf(HomeUiState::isRefreshing)

	override fun render(uiState: HomeUiState) {
		swipeRefreshLayout.isRefreshing = uiState.isRefreshing
	}

	private fun setupRefresh() = with(swipeRefreshLayout) {
		applyDistanceToTrigger()
		setOnRefreshListener {
			viewModel.dispatchEvent(RefreshRequested)
			hapticFeedbackCallback.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
		}
	}
}