package solru.okkeipatcher.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.databinding.FragmentWorkBinding
import solru.okkeipatcher.ui.utils.extensions.copyTextToClipboard
import solru.okkeipatcher.ui.utils.extensions.showWithLifecycle
import solru.okkeipatcher.viewmodels.WorkViewModel

abstract class WorkFragment<VM : WorkViewModel> : Fragment(R.layout.fragment_work) {

	protected abstract val viewModel: VM
	private val binding by viewBinding(FragmentWorkBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		super.onPrepareOptionsMenu(menu)
		menu.clear()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupNavigation()
		viewLifecycleOwner.lifecycle.addObserver(viewModel)
		viewLifecycleOwner.lifecycleScope.observeUiState()
	}

	override fun onStop() {
		super.onStop()
		viewModel.hideAllMessages()
	}

	protected abstract fun onSuccess()

	private fun setupNavigation() {
		binding.buttonWork.setOnClickListener {
			onButtonClick()
		}
	}

	private fun onButtonClick() {
		if (viewModel.isWorkRunning) {
			viewModel.cancel()
		} else {
			findNavController().popBackStack()
		}
	}

	private fun CoroutineScope.observeUiState() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			viewModel.uiState.collect { uiState ->
				if (uiState.isWorkSuccessful) {
					binding.buttonWork.setText(android.R.string.ok)
					onSuccess()
				}
				if (uiState.isWorkCanceled) {
					findNavController().popBackStack()
				}
				binding.textviewWorkStatus.text = uiState.status.resolve(requireContext())
				setProgress(uiState.progressData)
				uiState.startWorkMessage.run {
					if (!isVisible && data != null) {
						showStartWorkMessage(data)
					}
				}
				uiState.cancelWorkMessage.run {
					if (!isVisible && data != null) {
						showCancelWorkMessage(data)
					}
				}
				uiState.errorMessage.run {
					if (!isVisible && data != null) {
						showErrorMessage(data)
					}
				}
			}
		}
	}

	private fun setProgress(progressData: ProgressData) {
		binding.progressbarWork.max = progressData.max
		binding.progressbarWork.setProgressCompat(progressData.progress, true)
		binding.progressbarWork.isIndeterminate = progressData.isIndeterminate
	}

	private fun showStartWorkMessage(startWorkMessage: Message) {
		createDialogBuilder(startWorkMessage)
			.setPositiveButton(R.string.start) { _, _ ->
				viewModel.startWork()
			}
			.setNegativeButton(android.R.string.cancel) { _, _ ->
				findNavController().popBackStack()
			}
			.setOnCancelListener {
				findNavController().popBackStack()
			}
			.setOnDismissListener {
				viewModel.closeStartWorkMessage()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.showStartWorkMessage()
	}

	private fun showCancelWorkMessage(cancelWorkMessage: Message) {
		createDialogBuilder(cancelWorkMessage)
			.setPositiveButton(R.string.abort) { _, _ ->
				viewModel.cancelWork()
			}
			.setNegativeButton(android.R.string.cancel, null)
			.setOnDismissListener {
				viewModel.closeCancelWorkMessage()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.showCancelWorkMessage()
	}

	private fun showErrorMessage(errorMessage: Message) {
		val message = errorMessage.message.resolve(requireContext())
		createDialogBuilder(errorMessage)
			.setNeutralButton(R.string.dialog_button_copy_to_clipboard) { _, _ ->
				requireContext().copyTextToClipboard("Okkei Patcher Exception", message)
			}
			.setOnDismissListener {
				viewModel.closeErrorMessage()
				findNavController().popBackStack()
			}
			.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.showErrorMessage()
	}

	private fun createDialogBuilder(message: Message): AlertDialog.Builder {
		val titleString = message.title.resolve(requireContext())
		val messageString = message.message.resolve(requireContext())
		return AlertDialog.Builder(requireContext())
			.setCancelable(true)
			.setTitle(titleString)
			.setMessage(messageString)
	}
}