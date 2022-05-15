package ru.solrudev.okkeipatcher.ui.screen.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentHomeBinding
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.renderBy
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.*
import ru.solrudev.okkeipatcher.ui.util.extension.*

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
		viewLifecycleOwner.bindProgressButton(binding.buttonMainPatch)
		viewModel.renderBy(this)
		checkWorkResult()
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: HomeUiState) {
		binding.buttonMainPatch.isEnabled = uiState.isPatchEnabled
		binding.buttonMainRestore.isEnabled = uiState.isRestoreEnabled
		binding.buttonMainPatch.setLoading(uiState.isPatchSizeLoading, R.string.patch)
		if (uiState.pendingWork != null) {
			navigateToWorkScreen(uiState.pendingWork)
		}
		if (uiState.startPatchMessage.shouldShow) {
			showStartPatchMessage(uiState.startPatchMessage.data)
		}
		if (uiState.startRestoreMessage.shouldShow) {
			showStartRestoreMessage(uiState.startRestoreMessage.data)
		}
		if (uiState.patchUpdatesAvailable && uiState.canShowPatchUpdatesMessage) {
			showPatchUpdatesSnackbar()
		}
	}

	private fun setupNavigation() {
		binding.buttonMainPatch.attachTextChangeAnimator {
			fadeInMills = 150
			fadeOutMills = 150
		}
		binding.buttonMainPatch.setOnClickListener {
			viewModel.dispatchEvent(PatchRequested)
		}
		binding.buttonMainRestore.setOnClickListener {
			viewModel.dispatchEvent(RestoreRequested)
		}
	}

	private fun checkWorkResult() {
		val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle ?: return
		val result = savedStateHandle.getResult() ?: return
		viewModel.dispatchEvent(WorkFinished(result))
		savedStateHandle.clearResult()
	}

	private fun navigateToWorkScreen(work: Work) {
		val navController = findNavController()
		val workScreen = navController.findDestination(R.id.work_fragment)
		workScreen?.label = work.label.resolve(requireContext())
		val toWorkScreen = HomeFragmentDirections.actionHomeFragmentToWorkFragment(work)
		navController.navigate(toWorkScreen)
		viewModel.dispatchEvent(NavigatedToWorkScreen)
	}

	private fun showPatchUpdatesSnackbar() {
		view?.let {
			Snackbar.make(it, R.string.prompt_update_patch_available, Snackbar.LENGTH_LONG).show()
		}
		viewModel.dispatchEvent(PatchUpdatesMessageShown)
	}

	private fun showStartPatchMessage(startPatchMessage: Message) {
		requireContext().createDialogBuilder(startPatchMessage)
			.setPositiveButton(R.string.start) { _, _ ->
				viewModel.dispatchEvent(StartPatch)
			}
			.setNegativeButton(android.R.string.cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartPatchMessageDismissed)
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(StartPatchMessageShown)
	}

	private fun showStartRestoreMessage(startRestoreMessage: Message) {
		requireContext().createDialogBuilder(startRestoreMessage)
			.setPositiveButton(R.string.start) { _, _ ->
				viewModel.dispatchEvent(StartRestore)
			}
			.setNegativeButton(android.R.string.cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(StartRestoreMessageDismissed)
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(StartRestoreMessageShown)
	}
}