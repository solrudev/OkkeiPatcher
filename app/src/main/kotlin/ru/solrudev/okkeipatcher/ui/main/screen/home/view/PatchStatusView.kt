package ru.solrudev.okkeipatcher.ui.main.screen.home.view

import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.CardUpdateStatusBinding
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.util.localizedText

class PatchStatusView(private val binding: CardUpdateStatusBinding) : JetView<HomeUiState> {

	init {
		binding.buttonCardUpdate.setText(R.string.button_text_update_patch)
	}

	override val trackedState = listOf(HomeUiState::patchStatus, HomeUiState::patchUpdatesAvailable)

	override fun render(uiState: HomeUiState) = with(binding) {
		textviewCardUpdateStatus.localizedText = uiState.patchStatus
		buttonCardUpdate.isVisible = uiState.patchUpdatesAvailable
	}
}