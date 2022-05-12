package ru.solrudev.okkeipatcher.ui.fragment

import android.app.Dialog
import android.app.NotificationManager
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentWorkBinding
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.ui.util.extension.*
import ru.solrudev.okkeipatcher.ui.viewmodel.WorkViewModel

@AndroidEntryPoint
class WorkFragment : Fragment(R.layout.fragment_work) {

	private val viewModel by viewModels<WorkViewModel>()
	private val args by navArgs<WorkFragmentArgs>()
	private val binding by viewBinding(FragmentWorkBinding::bind)
	private var currentCancelDialog: Dialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
		findNavController().currentDestination?.label = args.work.label.resolve(requireContext())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		finishActivityOnBackPressed()
		prepareOptionsMenu {
			clear()
		}
		setupNavigation()
		viewLifecycleOwner.lifecycleScope.observeUiState()
	}

	override fun onStart() {
		super.onStart()
		viewModel.observeWork(args.work)
	}

	override fun onStop() {
		super.onStop()
		viewModel.stopObservingWork()
		currentCancelDialog = null
	}

	private fun setupNavigation() {
		binding.buttonWork.setOnClickListener {
			viewModel.promptCancelWork()
		}
	}

	private fun CoroutineScope.observeUiState() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			viewModel.collect { uiState ->
				binding.textviewWorkStatus.text = uiState.status.resolve(requireContext())
				setProgress(uiState.progressData)
				if (uiState.isWorkSuccessful) {
					onWorkSucceeded(playAnimations = !uiState.animationsPlayed)
				}
				if (uiState.isWorkCanceled) {
					onWorkCanceled()
				}
				if (uiState.cancelWorkMessage != Message.empty) {
					showCancelWorkMessage(uiState.cancelWorkMessage)
				}
				if (uiState.error != Message.empty) {
					onError(uiState.error)
				}
			}
		}
	}

	private fun clearNotifications() {
		val notificationManager = requireContext().getSystemService<NotificationManager>()
		notificationManager?.cancelAll()
	}

	private fun onWorkSucceeded(playAnimations: Boolean) {
		setResult(true)
		if (playAnimations) {
			startSuccessAnimations()
		}
		binding.buttonWork.setOnClickListener {
			findNavController().popBackStack()
		}
		binding.buttonWork.setText(android.R.string.ok)
		currentCancelDialog?.dismiss()
		clearNotifications()
	}

	private fun onWorkCanceled() {
		setResult(false)
		findNavController().popBackStack()
	}

	private fun onError(error: Message) {
		setResult(false)
		binding.buttonWork.isEnabled = false
		showErrorMessage(error)
		clearNotifications()
	}

	private fun startSuccessAnimations() {
		binding.buttonWork.alpha = 0f
		binding.buttonWork
			.animate()
			.alpha(1f)
			.setDuration(500)
			.setInterpolator(DecelerateInterpolator())
			.start()
		viewModel.onAnimationsPlayed()
	}

	private fun setProgress(progressData: ProgressData) {
		binding.progressbarWork.max = progressData.max
		binding.progressbarWork.setProgressCompat(progressData.progress, true)
		val percentDone = (progressData.progress.toDouble() / progressData.max * 100).toInt()
		binding.textviewWorkPercentDone.text = getString(R.string.percent_done, percentDone)
	}

	private fun showCancelWorkMessage(cancelWorkMessage: Message) {
		val dialog = requireContext().createDialogBuilder(cancelWorkMessage)
			.setPositiveButton(R.string.abort) { _, _ ->
				viewModel.cancelWork(args.work)
			}
			.setNegativeButton(android.R.string.cancel, null)
			.setOnDismissListener {
				viewModel.dismissCancelWorkMessage()
				currentCancelDialog = null
			}
			.create()
		currentCancelDialog = dialog
		dialog.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
	}

	private fun showErrorMessage(errorMessage: Message) {
		val message = errorMessage.message.resolve(requireContext())
		requireContext().createDialogBuilder(errorMessage)
			.setNeutralButton(R.string.dialog_button_copy_to_clipboard) { _, _ ->
				requireContext().copyTextToClipboard("Okkei Patcher Exception", message)
			}
			.setOnDismissListener {
				viewModel.dismissErrorMessage()
				findNavController().popBackStack()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
	}

	private fun setResult(value: Boolean) =
		findNavController().previousBackStackEntry?.savedStateHandle?.setResult(value)
}