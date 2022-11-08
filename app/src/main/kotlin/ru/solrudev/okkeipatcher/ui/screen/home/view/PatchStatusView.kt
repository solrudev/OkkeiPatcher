package ru.solrudev.okkeipatcher.ui.screen.home.view

import android.content.Context
import androidx.core.view.isVisible
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.CardPatchStatusBinding
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState

class PatchStatusView(private val binding: CardPatchStatusBinding) : JetView<HomeUiState> {

	private val context: Context
		get() = binding.root.context

	override val trackedState = listOf(HomeUiState::patchStatus, HomeUiState::patchUpdatesAvailable)

	override fun render(uiState: HomeUiState) = with(binding) {
		textviewCardPatchStatus.text = uiState.patchStatus.resolve(context)
		textviewCardPatchUpdate.isVisible = uiState.patchUpdatesAvailable
	}
}