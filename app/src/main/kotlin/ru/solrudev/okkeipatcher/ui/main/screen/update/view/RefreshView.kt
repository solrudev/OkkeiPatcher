package ru.solrudev.okkeipatcher.ui.main.screen.update.view

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.update.UpdateViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState

class RefreshView(
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

	private fun setupRefresh() {
		swipeRefreshLayout.setOnRefreshListener {
			viewModel.dispatchEvent(UpdateDataRequested(refresh = true))
		}
	}
}