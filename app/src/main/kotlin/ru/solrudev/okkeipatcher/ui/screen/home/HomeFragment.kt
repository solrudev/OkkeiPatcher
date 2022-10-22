package ru.solrudev.okkeipatcher.ui.screen.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.CardActionsBinding
import ru.solrudev.okkeipatcher.databinding.CardGameInfoBinding
import ru.solrudev.okkeipatcher.databinding.CardPatchStatusBinding
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
	private val gameInfoBinding by viewBinding(CardGameInfoBinding::bind, R.id.container_card_game)
	private val actionsBinding by viewBinding(CardActionsBinding::bind, R.id.container_card_actions)
	private val patchStatusBinding by viewBinding(CardPatchStatusBinding::bind, R.id.container_card_patch)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupNavigation()
		binding.containerHome.animateLayoutChanges()
		viewLifecycleOwner.bindProgressButton(actionsBinding.buttonCardActionsPatch)
		loadGameInfo()
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: HomeUiState) {
		actionsBinding.buttonCardActionsPatch.isEnabled = uiState.isPatchEnabled
		actionsBinding.buttonCardActionsRestore.isEnabled = uiState.isRestoreEnabled
		actionsBinding.buttonCardActionsPatch.setLoading(uiState.isPatchSizeLoading, R.string.button_text_patch)
		patchStatusBinding.textviewCardPatchStatus.text = uiState.patchStatus.resolve(requireContext())
		patchStatusBinding.textviewCardPatchUpdate.isVisible = uiState.patchUpdatesAvailable
		displayPatchVersion(uiState.patchVersion)
		if (uiState.startPatchMessage.shouldShow) {
			showStartPatchMessage(uiState.startPatchMessage.data)
		}
		if (uiState.startRestoreMessage.shouldShow) {
			showStartRestoreMessage(uiState.startRestoreMessage.data)
		}
	}

	private fun setupNavigation() {
		actionsBinding.buttonCardActionsPatch.attachTextChangeAnimator {
			fadeInMills = 150
			fadeOutMills = 150
		}
		actionsBinding.buttonCardActionsPatch.setOnClickListener {
			viewModel.dispatchEvent(PatchRequested)
		}
		actionsBinding.buttonCardActionsRestore.setOnClickListener {
			viewModel.dispatchEvent(RestoreRequested)
		}
		patchStatusBinding.textviewCardPatchUpdate.setOnClickListener {
			actionsBinding.buttonCardActionsPatch.showRippleEffect()
		}
	}

	private fun loadGameInfo() {
		val gameUiState = GameUiState(requireContext())
		gameInfoBinding.textviewCardGameTitle.text = gameUiState.title
		gameInfoBinding.textviewCardGameVersion.text = getString(R.string.card_game_version, gameUiState.version)
		gameInfoBinding.imageviewCardGameIcon.setImageDrawable(gameUiState.icon)
	}

	private fun displayPatchVersion(patchVersion: String) {
		val version = patchVersion.ifEmpty { getString(R.string.not_available) }
		gameInfoBinding.textviewCardGamePatch.text = getString(R.string.card_patch_version, version)
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