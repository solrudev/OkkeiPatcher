package ru.solrudev.okkeipatcher.ui.screen.home.view

import androidx.lifecycle.LifecycleOwner
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.CardActionsBinding
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.PatchRequested
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.RestoreRequested
import ru.solrudev.okkeipatcher.ui.util.setLoading
import ru.solrudev.okkeipatcher.ui.util.setupProgressButton

class ActionsView(
	lifecycleOwner: LifecycleOwner,
	private val binding: CardActionsBinding,
	private val viewModel: HomeViewModel
) : FeatureView<HomeUiState> {

	init {
		binding.buttonCardActionsPatch.setupProgressButton(lifecycleOwner)
		setupNavigation()
	}

	override fun render(uiState: HomeUiState) = with(binding) {
		buttonCardActionsPatch.isEnabled = uiState.isPatchEnabled
		buttonCardActionsRestore.isEnabled = uiState.isRestoreEnabled
		buttonCardActionsPatch.setLoading(uiState.isPatchSizeLoading, R.string.button_text_patch)
	}

	private fun setupNavigation() = with(binding) {
		buttonCardActionsPatch.setOnClickListener {
			viewModel.dispatchEvent(PatchRequested)
		}
		buttonCardActionsRestore.setOnClickListener {
			viewModel.dispatchEvent(RestoreRequested)
		}
	}
}