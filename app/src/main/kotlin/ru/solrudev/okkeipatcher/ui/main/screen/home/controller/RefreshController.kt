package ru.solrudev.okkeipatcher.ui.main.screen.home.controller

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.PatchUpdatesRequested

class RefreshController(
	private val swipeRefreshLayout: SwipeRefreshLayout,
	private val viewModel: HomeViewModel
) : JetView<HomeUiState> {

	init {
		setupRefresh()
	}

	override val trackedState = listOf(HomeUiState::isPatchUpdateLoading)

	override fun render(uiState: HomeUiState) {
		swipeRefreshLayout.isRefreshing = uiState.isPatchUpdateLoading
	}

	private fun setupRefresh() {
		swipeRefreshLayout.setOnRefreshListener {
			viewModel.dispatchEvent(PatchUpdatesRequested)
		}
	}
}