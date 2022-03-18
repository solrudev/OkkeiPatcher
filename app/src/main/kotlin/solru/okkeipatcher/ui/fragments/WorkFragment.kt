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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.databinding.FragmentWorkBinding
import solru.okkeipatcher.ui.utils.extensions.copyTextToClipboard
import solru.okkeipatcher.ui.utils.extensions.indeterminate
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
		viewLifecycleOwner.lifecycleScope.observeViewModel()
	}

	protected abstract fun onSuccess()

	private fun setupNavigation() {
		binding.buttonWork.setOnClickListener {
			onButtonClick()
		}
	}

	private fun onButtonClick() {
		if (viewModel.isWorkRunning) {
			viewModel.showCancelWarning()
		} else {
			findNavController().popBackStack()
		}
	}

	private fun CoroutineScope.observeViewModel() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			observeStartWorkMessage()
			observeCancelWorkMessage()
			observeErrorMessage()
			observeStatus()
			observeProgress()
			observeButtonText()
			observeWorkSucceeded()
			observeWorkCanceled()
		}
	}

	private fun CoroutineScope.observeStartWorkMessage() = launch {
		viewModel.startWorkMessage.collect { startMessage ->
			createDialogBuilder(startMessage)
				.setPositiveButton(android.R.string.ok) { _, _ ->
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
		}
	}

	private fun CoroutineScope.observeCancelWorkMessage() = launch {
		viewModel.cancelWorkMessage.collect { cancelMessage ->
			createDialogBuilder(cancelMessage)
				.setPositiveButton(android.R.string.ok) { _, _ ->
					viewModel.cancelWork()
				}
				.setNegativeButton(android.R.string.cancel, null)
				.setOnDismissListener {
					viewModel.closeCancelWorkMessage()
				}
				.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		}
	}

	private fun CoroutineScope.observeStatus() = launch {
		viewModel.status.collect {
			binding.textviewWorkStatus.text = it.resolve(requireContext())
		}
	}

	private fun CoroutineScope.observeProgress() = launch {
		viewModel.progressData.collect {
			binding.progressbarWork.max = it.max
			binding.progressbarWork.setProgressCompat(it.progress, true)
			binding.progressbarWork.indeterminate = it.isIndeterminate
		}
	}

	private fun CoroutineScope.observeErrorMessage() = launch {
		viewModel.errorMessage.collectLatest { error ->
			val message = error.message.resolve(requireContext())
			createDialogBuilder(error)
				.setNeutralButton(R.string.dialog_button_copy_to_clipboard) { _, _ ->
					requireContext().copyTextToClipboard("Okkei Patcher Exception", message)
				}
				.setOnDismissListener {
					viewModel.closeErrorMessage()
					findNavController().popBackStack()
				}
				.showWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.Event.ON_STOP)
		}
	}

	private fun CoroutineScope.observeButtonText() = launch {
		viewModel.buttonText.collect {
			binding.buttonWork.text = it.resolve(requireContext())
		}
	}

	private fun CoroutineScope.observeWorkSucceeded() = launch {
		viewModel.workSucceeded.collect {
			onSuccess()
		}
	}

	private fun CoroutineScope.observeWorkCanceled() = launch {
		viewModel.workCanceled.collect {
			findNavController().popBackStack()
		}
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