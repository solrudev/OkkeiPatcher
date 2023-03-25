package ru.solrudev.okkeipatcher.ui.main.screen.home.view

import android.content.Context
import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.CardUpdateStatusBinding
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState

class PatchStatusView(private val binding: CardUpdateStatusBinding) : JetView<HomeUiState> {

	init {
		binding.buttonCardUpdate.setText(R.string.button_text_update_patch)
	}

	private val context: Context
		get() = binding.root.context

	override val trackedState = listOf(HomeUiState::patchStatus, HomeUiState::patchUpdatesAvailable)

	override fun render(uiState: HomeUiState) = with(binding) {
		textviewCardUpdateStatus.text = uiState.patchStatus.resolve(context)
		buttonCardUpdate.isVisible = uiState.patchUpdatesAvailable
	}
}