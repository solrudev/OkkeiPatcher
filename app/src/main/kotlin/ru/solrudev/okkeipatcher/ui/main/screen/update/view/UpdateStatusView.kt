package ru.solrudev.okkeipatcher.ui.main.screen.update.view

import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.databinding.CardUpdateStatusBinding
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.util.localizedText

class UpdateStatusView(
	private val binding: CardUpdateStatusBinding
) : JetView<UpdateUiState> {

	override val trackedState = listOf(
		UpdateUiState::status,
		UpdateUiState::buttonText,
		UpdateUiState::isUpdateButtonVisible,
		UpdateUiState::isUpdateButtonEnabled,
		UpdateUiState::isUpdating
	)

	override fun render(uiState: UpdateUiState) = with(binding) {
		buttonCardUpdate.isVisible = uiState.isUpdateButtonVisible
		buttonCardUpdate.isEnabled = uiState.isUpdateButtonEnabled
		buttonCardUpdate.localizedText = uiState.buttonText
		buttonCardUpdate.setAbortEnabled(uiState.isUpdating, animate = false)
		textviewCardUpdateStatus.localizedText = uiState.status
	}
}