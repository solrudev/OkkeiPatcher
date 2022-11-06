package ru.solrudev.okkeipatcher.ui.screen.home.view

import android.content.Context
import androidx.lifecycle.Lifecycle
import io.github.solrudev.jetmvi.FeatureView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.*
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

class RestoreMessageView(
	private val context: Context,
	private val viewLifecycle: Lifecycle,
	private val viewModel: HomeViewModel
) : FeatureView<HomeUiState> {

	override val trackedUiState = listOf(HomeUiState::startRestoreMessage)

	override fun render(uiState: HomeUiState) {
		if (uiState.startRestoreMessage.shouldShow) {
			showStartRestoreMessage(uiState.startRestoreMessage.data)
		}
	}

	private fun showStartRestoreMessage(startRestoreMessage: Message) {
		context.createDialogBuilder(startRestoreMessage)
			.setIcon(R.drawable.ic_start_work)
			.setPositiveButton(R.string.button_text_start) { _, _ ->
				viewModel.dispatchEvent(StartRestore)
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartRestoreMessageDismissed)
			}
			.showWithLifecycle(viewLifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(StartRestoreMessageShown)
	}
}