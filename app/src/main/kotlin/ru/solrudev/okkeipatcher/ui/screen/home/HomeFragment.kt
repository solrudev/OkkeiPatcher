package ru.solrudev.okkeipatcher.ui.screen.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentHomeBinding
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.renderBy
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchUpdatesMessageShown
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.shouldShowPatchUpdatesMessage
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.setLoading
import ru.solrudev.okkeipatcher.ui.util.setupTransitions
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), FeatureView<HomeUiState> {

	private val viewModel by viewModels<HomeViewModel>()
	private val binding by viewBinding(FragmentHomeBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupNavigation()
		viewLifecycleOwner.bindProgressButton(binding.buttonHomePatch)
		viewModel.renderBy(this)
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: HomeUiState) {
		binding.buttonHomePatch.isEnabled = uiState.isPatchEnabled
		binding.buttonHomeRestore.isEnabled = uiState.isRestoreEnabled
		binding.buttonHomePatch.setLoading(uiState.isPatchSizeLoading, R.string.button_text_patch)
		if (uiState.startPatchMessage.shouldShow) {
			showStartPatchMessage(uiState.startPatchMessage.data)
		}
		if (uiState.startRestoreMessage.shouldShow) {
			showStartRestoreMessage(uiState.startRestoreMessage.data)
		}
		if (uiState.shouldShowPatchUpdatesMessage) {
			showPatchUpdatesSnackbar()
		}
	}

	private fun setupNavigation() {
		binding.buttonHomePatch.attachTextChangeAnimator {
			fadeInMills = 150
			fadeOutMills = 150
		}
		binding.buttonHomePatch.setOnClickListener {
			viewModel.dispatchEvent(PatchRequested)
		}
		binding.buttonHomeRestore.setOnClickListener {
			viewModel.dispatchEvent(RestoreRequested)
		}
	}

	private fun showPatchUpdatesSnackbar() {
		view?.let {
			Snackbar.make(it, R.string.snackbar_patch_update_available, Snackbar.LENGTH_LONG).show()
		}
		viewModel.dispatchEvent(PatchUpdatesMessageShown)
	}

	private fun showStartPatchMessage(startPatchMessage: Message) {
		requireContext().createDialogBuilder(startPatchMessage)
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