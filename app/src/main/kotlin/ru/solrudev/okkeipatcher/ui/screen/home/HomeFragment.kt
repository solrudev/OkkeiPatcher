package ru.solrudev.okkeipatcher.ui.screen.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentHomeBinding
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.derivedView
import ru.solrudev.okkeipatcher.ui.core.featureViewModels
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.view.ActionsView
import ru.solrudev.okkeipatcher.ui.screen.home.view.GameInfoView
import ru.solrudev.okkeipatcher.ui.screen.home.view.PatchStatusView
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showRippleEffect
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), FeatureView<HomeUiState> {

	private val binding by viewBinding(FragmentHomeBinding::bind)
	private val gameInfoView by derivedView { GameInfoView(binding.cardHomeGameInfo) }
	private val actionsView by derivedView { ActionsView(viewLifecycleOwner, binding.cardHomeActions, viewModel) }
	private val patchStatusView by derivedView { PatchStatusView(binding.cardHomePatchStatus) }

	private val viewModel: HomeViewModel by featureViewModels(
		HomeFragment::gameInfoView,
		HomeFragment::actionsView,
		HomeFragment::patchStatusView
	)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		setupNavigation()
		containerHome.animateLayoutChanges()
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: HomeUiState) {
		if (uiState.startPatchMessage.shouldShow) {
			showStartPatchMessage(uiState.startPatchMessage.data)
		}
		if (uiState.startRestoreMessage.shouldShow) {
			showStartRestoreMessage(uiState.startRestoreMessage.data)
		}
	}

	private fun setupNavigation() = with(binding) {
		cardHomePatchStatus.textviewCardPatchUpdate.setOnClickListener {
			cardHomeActions.buttonCardActionsPatch.showRippleEffect()
		}
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