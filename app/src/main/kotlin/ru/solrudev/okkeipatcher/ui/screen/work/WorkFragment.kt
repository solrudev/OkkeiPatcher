package ru.solrudev.okkeipatcher.ui.screen.work

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.FragmentWorkBinding
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.*
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import ru.solrudev.okkeipatcher.ui.screen.work.model.percentDone
import ru.solrudev.okkeipatcher.ui.shared.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.*

@AndroidEntryPoint
class WorkFragment : Fragment(R.layout.fragment_work), JetView<WorkUiState> {

	private val viewModel: WorkViewModel by jetViewModels()
	private val args by navArgs<WorkFragmentArgs>()
	private val binding by viewBinding(FragmentWorkBinding::bind)
	private var currentCancelDialog: Dialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setWorkLabel()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		containerWork.animateLayoutChanges()
		applyInsets()
		onBackPressed {
			requireActivity().finish()
		}
		setupNavigation()
		lottieAnimationViewWork.playAnimation()
	}

	override fun onStart() {
		super.onStart()
		viewModel.dispatchEvent(StartObservingWork(args.work))
	}

	override fun onStop() {
		super.onStop()
		currentCancelDialog = null
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: WorkUiState) = with(binding.cardProgressWork) {
		textviewWorkStatus.localizedText = uiState.status
		textviewWorkPercentDone.text = getString(R.string.percent_done, uiState.percentDone)
		setProgress(uiState.progressData)
		if (uiState.isWorkSuccessful) {
			onWorkSucceeded(playAnimations = !uiState.animationsPlayed)
		}
		if (uiState.isWorkCanceled) {
			onWorkCanceled()
		}
		if (uiState.cancelWorkMessage.shouldShow) {
			showCancelWorkMessage(uiState.cancelWorkMessage.data)
		}
		if (uiState.errorMessage.shouldShow) {
			onError(uiState.errorMessage.data, playAnimations = !uiState.animationsPlayed)
		}
	}

	private fun applyInsets() = with(binding) {
		buttonWork.applyInsetter {
			type(navigationBars = true) {
				margin()
			}
		}
	}

	private fun setupNavigation() = with(binding) {
		buttonWork.setOnClickListener {
			viewModel.dispatchEvent(CancelRequested)
		}
	}

	private fun setWorkLabel() {
		findNavController().currentDestination?.label = args.work.label.resolve(requireContext())
	}

	private fun onWorkSucceeded(playAnimations: Boolean) = with(binding) {
		startAnimation("success.lottie", play = playAnimations)
		buttonWork.setOnClickListener {
			findNavController().popBackStack()
		}
		buttonWork.setAbortEnabled(abortEnabled = false, animate = playAnimations)
		currentCancelDialog?.dismiss()
	}

	private fun onWorkCanceled() {
		findNavController().popBackStack()
	}

	private fun onError(error: Message, playAnimations: Boolean) = with(binding) {
		startAnimation("error.lottie", play = playAnimations)
		buttonWork.isEnabled = false
		currentCancelDialog?.dismiss()
		showErrorMessage(error)
	}

	private fun startAnimation(fileName: String, play: Boolean) = with(binding) {
		lottieAnimationViewWork.run {
			setOneshotAnimation(fileName, start = play)
			onAnimationEnd {
				viewModel.dispatchEvent(AnimationsPlayed)
				removeAllAnimatorListeners()
			}
		}
	}

	private fun setProgress(progressData: ProgressData) = with(binding.cardProgressWork) {
		progressbarWork.max = progressData.max
		progressbarWork.setProgressCompat(progressData.progress, true)
	}

	private fun showCancelWorkMessage(cancelWorkMessage: Message) {
		val dialog = requireContext().createDialogBuilder(cancelWorkMessage)
			.setIcon(R.drawable.ic_cancel)
			.setPositiveButton(R.string.button_text_abort) { _, _ ->
				viewModel.dispatchEvent(CancelWork(args.work))
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(CancelMessageDismissed)
				currentCancelDialog = null
			}
			.create()
		currentCancelDialog = dialog
		dialog.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(CancelMessageShown)
	}

	private fun showErrorMessage(errorMessage: Message) {
		val message = errorMessage.text.resolve(requireContext())
		requireContext().createDialogBuilder(errorMessage)
			.setIcon(R.drawable.ic_error)
			.setNeutralButton(R.string.button_text_copy_to_clipboard) { _, _ ->
				requireContext().copyTextToClipboard("Okkei Patcher Exception", message)
			}
			.setOnDismissListener {
				viewModel.dispatchEvent(ErrorDismissed)
				findNavController().popBackStack()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(ErrorShown)
	}
}