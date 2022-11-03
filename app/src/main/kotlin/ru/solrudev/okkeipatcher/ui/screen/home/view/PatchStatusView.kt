package ru.solrudev.okkeipatcher.ui.screen.home.view

import android.content.Context
import androidx.core.view.isVisible
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.CardPatchStatusBinding
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState

class PatchStatusView(private val binding: CardPatchStatusBinding) : FeatureView<HomeUiState> {

	private val context: Context
		get() = binding.root.context

	override fun render(uiState: HomeUiState) = with(binding) {
		textviewCardPatchStatus.text = uiState.patchStatus.resolve(context)
		textviewCardPatchUpdate.isVisible = uiState.patchUpdatesAvailable
	}
}