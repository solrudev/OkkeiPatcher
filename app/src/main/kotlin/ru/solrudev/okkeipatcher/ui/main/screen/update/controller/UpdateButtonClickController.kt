package ru.solrudev.okkeipatcher.ui.main.screen.update.controller

import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.ui.main.screen.update.UpdateViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.widget.AbortButton

class UpdateButtonClickController(
	private val button: AbortButton,
	private val viewModel: UpdateViewModel
) : JetView<UpdateUiState> {

	private var work: Work? = null

	override val trackedState = listOf(
		UpdateUiState::currentWork,
		UpdateUiState::isUpdating,
		UpdateUiState::isUpdateAvailable,
		UpdateUiState::isInstallPending
	)

	override fun render(uiState: UpdateUiState) {
		work = uiState.currentWork
		setButtonOnClickListener(uiState)
	}

	private fun setButtonOnClickListener(uiState: UpdateUiState) = with(button) {
		when {
			uiState.isUpdateAvailable -> setOnClickListener {
				viewModel.dispatchEvent(UpdateDownloadRequested)
			}
			uiState.isInstallPending -> setOnClickListener {
				viewModel.dispatchEvent(UpdateInstallRequested)
			}
			uiState.isUpdating -> setOnClickListener {
				work?.let { work ->
					viewModel.dispatchEvent(CancelWork(work))
				}
			}
		}
	}
}