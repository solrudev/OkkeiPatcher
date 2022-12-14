package ru.solrudev.okkeipatcher.ui.main.screen.home.view

import android.content.Context
import androidx.lifecycle.Lifecycle
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.main.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

class PatchMessageView(
	private val context: Context,
	private val viewLifecycle: Lifecycle,
	private val viewModel: HomeViewModel
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
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartPatchMessageDismissed)
			}
			.showWithLifecycle(viewLifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(StartPatchMessageShown)
	}
}