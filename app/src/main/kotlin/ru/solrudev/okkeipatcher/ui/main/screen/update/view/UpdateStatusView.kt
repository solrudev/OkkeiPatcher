package ru.solrudev.okkeipatcher.ui.main.screen.update.view

import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.CardUpdateStatusBinding
import ru.solrudev.okkeipatcher.ui.main.screen.update.UpdateViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState

class UpdateStatusView(
	private val binding: CardUpdateStatusBinding,
	private val viewModel: UpdateViewModel
) : JetView<UpdateUiState> {

	private val context by binding.root::context
	private var work: Work? = null

	override val trackedState = listOf(
		UpdateUiState::currentWork,
		UpdateUiState::status,
		UpdateUiState::buttonText,
		UpdateUiState::isUpdateButtonVisible,
		UpdateUiState::isUpdateButtonEnabled,
		UpdateUiState::isUpdating,
		UpdateUiState::isUpdateAvailable,
		UpdateUiState::isInstallPending
	)

	override fun render(uiState: UpdateUiState) = with(binding) {
		work = uiState.currentWork
		buttonCardUpdate.isVisible = uiState.isUpdateButtonVisible
		buttonCardUpdate.isEnabled = uiState.isUpdateButtonEnabled
		buttonCardUpdate.text = uiState.buttonText.resolve(context).toString()
		buttonCardUpdate.setAbortEnabled(uiState.isUpdating, animate = false)
		setButtonOnClickListener(uiState)
		textviewCardUpdateStatus.text = uiState.status.resolve(context)
	}

	private fun setButtonOnClickListener(uiState: UpdateUiState) = with(binding) {
		when {
			uiState.isUpdateAvailable -> buttonCardUpdate.setOnClickListener {
				viewModel.dispatchEvent(UpdateDownloadRequested)
			}
			uiState.isInstallPending -> buttonCardUpdate.setOnClickListener {
				viewModel.dispatchEvent(UpdateInstallRequested)
			}
			uiState.isUpdating -> buttonCardUpdate.setOnClickListener {
				work?.let { work ->
					viewModel.dispatchEvent(CancelWork(work))
				}
			}
		}
	}
}