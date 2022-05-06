package ru.solrudev.okkeipatcher.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentHomeBinding
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.extension.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.extension.setLoading
import ru.solrudev.okkeipatcher.ui.util.extension.setupTransitions
import ru.solrudev.okkeipatcher.ui.util.extension.showWithLifecycle
import ru.solrudev.okkeipatcher.ui.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

	private val viewModel by viewModels<HomeViewModel>()
	private val binding by viewBinding(FragmentHomeBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupNavigation()
		viewLifecycleOwner.bindProgressButton(binding.buttonMainPatch)
		viewLifecycleOwner.lifecycle.addObserver(viewModel)
		viewLifecycleOwner.lifecycleScope.observeUiState()
	}

	private fun setupNavigation() {
		binding.buttonMainPatch.attachTextChangeAnimator {
			fadeInMills = 100
			fadeOutMills = 100
		}
		binding.buttonMainPatch.setOnClickListener {
			viewModel.promptPatch()
		}
		binding.buttonMainRestore.setOnClickListener {
			viewModel.promptRestore()
		}
	}

	private fun CoroutineScope.observeUiState() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			viewModel.collect { uiState ->
				if (uiState.pendingWork != null) {
					navigateToWorkScreen(uiState.pendingWork)
				}
				binding.buttonMainPatch.isEnabled = uiState.isPatchEnabled
				binding.buttonMainRestore.isEnabled = uiState.isRestoreEnabled
				if (!uiState.checkedForPatchUpdates) {
					viewModel.checkPatchUpdates()
				}
				if (uiState.patchUpdatesAvailable && !uiState.patchUpdatesMessageShown) {
					showPatchUpdatesSnackbar()
				}
				binding.buttonMainPatch.setLoading(uiState.isPatchSizeLoading, R.string.patch)
				if (uiState.startPatchMessage.shouldShow) {
					showStartPatchMessage(uiState.startPatchMessage.data)
				}
				if (uiState.startRestoreMessage.shouldShow) {
					showStartRestoreMessage(uiState.startRestoreMessage.data)
				}
			}
		}
	}

	private fun navigateToWorkScreen(work: Work) {
		val navController = findNavController()
		val workScreen = navController.findDestination(R.id.work_fragment)
		workScreen?.label = work.label.resolve(requireContext())
		val toWorkScreen = HomeFragmentDirections.actionHomeFragmentToWorkFragment(work)
		navController.navigate(toWorkScreen)
		viewModel.navigatedToWorkScreen()
	}

	private fun showPatchUpdatesSnackbar() {
		view?.let {
			Snackbar.make(it, R.string.prompt_update_patch_available, Snackbar.LENGTH_LONG).show()
		}
		viewModel.patchUpdatesMessageShown()
	}

	private fun showStartPatchMessage(startPatchMessage: Message) {
		requireContext().createDialogBuilder(startPatchMessage)
			.setPositiveButton(R.string.start) { _, _ ->
				viewModel.startPatch()
			}
			.setNegativeButton(android.R.string.cancel, null)
			.setOnDismissListener {
				viewModel.dismissStartPatchMessage()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.showStartPatchMessage()
	}

	private fun showStartRestoreMessage(startRestoreMessage: Message) {
		requireContext().createDialogBuilder(startRestoreMessage)
			.setPositiveButton(R.string.start) { _, _ ->
				viewModel.startRestore()
			}
			.setNegativeButton(android.R.string.cancel, null)
			.setOnDismissListener {
				viewModel.dismissStartRestoreMessage()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.showStartRestoreMessage()
	}
}