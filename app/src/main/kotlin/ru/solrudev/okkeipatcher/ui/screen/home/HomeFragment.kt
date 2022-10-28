package ru.solrudev.okkeipatcher.ui.screen.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.FragmentHomeBinding
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.featureViewModels
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.home.model.GameUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.*
import ru.solrudev.okkeipatcher.ui.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), FeatureView<HomeUiState> {

	private val viewModel: HomeViewModel by featureViewModels()
	private val binding by viewBinding(FragmentHomeBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		setupNavigation()
		cardHomeActions.buttonCardActionsPatch.setupProgressButton(viewLifecycleOwner)
		containerHome.animateLayoutChanges()
		loadGameInfo()
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: HomeUiState) {
		renderActions(uiState)
		renderPatchStatus(uiState)
		displayPatchVersion(uiState.patchVersion)
		if (uiState.startPatchMessage.shouldShow) {
			showStartPatchMessage(uiState.startPatchMessage.data)
		}
		if (uiState.startRestoreMessage.shouldShow) {
			showStartRestoreMessage(uiState.startRestoreMessage.data)
		}
	}

	private fun renderActions(uiState: HomeUiState) = with(binding.cardHomeActions) {
		buttonCardActionsPatch.isEnabled = uiState.isPatchEnabled
		buttonCardActionsRestore.isEnabled = uiState.isRestoreEnabled
		buttonCardActionsPatch.setLoading(uiState.isPatchSizeLoading, R.string.button_text_patch)
	}

	private fun renderPatchStatus(uiState: HomeUiState) = with(binding.cardHomePatchStatus) {
		textviewCardPatchStatus.text = uiState.patchStatus.resolve(requireContext())
		textviewCardPatchUpdate.isVisible = uiState.patchUpdatesAvailable
	}

	private fun setupNavigation() = with(binding) {
		setupActionsNavigation()
		cardHomePatchStatus.textviewCardPatchUpdate.setOnClickListener {
			cardHomeActions.buttonCardActionsPatch.showRippleEffect()
		}
	}

	private fun setupActionsNavigation() = with(binding.cardHomeActions) {
		buttonCardActionsPatch.setOnClickListener {
			viewModel.dispatchEvent(PatchRequested)
		}
		buttonCardActionsRestore.setOnClickListener {
			viewModel.dispatchEvent(RestoreRequested)
		}
	}

	private fun loadGameInfo() = with(binding.cardHomeGameInfo) {
		val gameUiState = GameUiState(requireContext())
		textviewCardGameTitle.text = gameUiState.title
		textviewCardGameVersion.text = getString(R.string.card_game_version, gameUiState.version)
		imageviewCardGameIcon.setImageDrawable(gameUiState.icon)
	}

	private fun displayPatchVersion(patchVersion: String) = with(binding.cardHomeGameInfo) {
		val version = patchVersion.ifEmpty { getString(R.string.not_available) }
		textviewCardGamePatch.text = getString(R.string.card_patch_version, version)
	}

	private fun showStartPatchMessage(startPatchMessage: Message) {
		requireContext().createDialogBuilder(startPatchMessage)
			.setIcon(R.drawable.ic_start_work)
			.setPositiveButton(R.string.button_text_start) { _, _ ->
				viewModel.dispatchEvent(StartPatch)
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartPatchMessageDismissed)
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(StartPatchMessageShown)
	}

	private fun showStartRestoreMessage(startRestoreMessage: Message) {
		requireContext().createDialogBuilder(startRestoreMessage)
			.setIcon(R.drawable.ic_start_work)
			.setPositiveButton(R.string.button_text_start) { _, _ ->
				viewModel.dispatchEvent(StartRestore)
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartRestoreMessageDismissed)
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(StartRestoreMessageShown)
	}
}