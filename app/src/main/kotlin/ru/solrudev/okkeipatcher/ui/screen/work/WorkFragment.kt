/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.ui.screen.work

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.AnimationsPlayed
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelMessageDismissed
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelMessageShown
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelRequested
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.ErrorDismissed
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.ErrorShown
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.StartObservingWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import ru.solrudev.okkeipatcher.ui.screen.work.model.percentDone
import ru.solrudev.okkeipatcher.ui.shared.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges
import ru.solrudev.okkeipatcher.ui.util.copyTextToClipboard
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.doOnAnimationEnd
import ru.solrudev.okkeipatcher.ui.util.localizedText
import ru.solrudev.okkeipatcher.ui.util.onBackPressed
import ru.solrudev.okkeipatcher.ui.util.performHapticContextClick
import ru.solrudev.okkeipatcher.ui.util.setOneshotAnimation
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

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
		handleAbortRequest()
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
		containerWork.applyInsetter {
			type(displayCutout = true) {
				padding(left = true, right = true)
			}
		}
		buttonWork.applyInsetter {
			type(navigationBars = true, displayCutout = true) {
				margin(bottom = true)
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

	private fun handleAbortRequest() {
		if (args.isAbortRequested) {
			arguments = WorkFragmentArgs(args.work, isAbortRequested = false).toBundle()
			viewModel.dispatchEvent(CancelRequested)
		}
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
			doOnAnimationEnd {
				viewModel.dispatchEvent(AnimationsPlayed)
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
				performHapticContextClick()
			}
			.setNegativeButton(R.string.button_text_cancel, null)
			.setOnDismissListener {
				viewModel.dispatchEvent(CancelMessageDismissed)
				currentCancelDialog = null
			}
			.create()
		currentCancelDialog = dialog
		dialog.showWithLifecycle(viewLifecycleOwner.lifecycle)
		viewModel.dispatchEvent(CancelMessageShown)
	}

	private fun showErrorMessage(errorMessage: Message) {
		val message = errorMessage.text.resolve(requireContext())
		requireContext().createDialogBuilder(errorMessage)
			.setIcon(R.drawable.ic_error)
			.setNeutralButton(R.string.button_text_copy_to_clipboard) { _, _ ->
				requireContext().copyTextToClipboard("Okkei Patcher Exception", message)
				performHapticContextClick()
			}
			.setOnDismissListener {
				viewModel.dispatchEvent(ErrorDismissed)
				findNavController().popBackStack()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle)
		viewModel.dispatchEvent(ErrorShown)
	}
}