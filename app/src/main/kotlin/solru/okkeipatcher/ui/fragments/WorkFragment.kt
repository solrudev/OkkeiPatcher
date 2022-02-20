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
import solru.okkeipatcher.utils.extensions.copyTextToClipboard
import solru.okkeipatcher.viewmodels.WorkViewModel

abstract class WorkFragment : Fragment(R.layout.fragment_work) {

	protected abstract val viewModel: WorkViewModel
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
		if (savedInstanceState == null && !viewModel.isWorkRunning) {
			viewModel.startWork()
		}
	}

	protected abstract fun onSuccess()

	private fun setupNavigation() {
		binding.buttonWork.setOnClickListener {
			onButtonClick()
		}
	}

	private fun onButtonClick() {
		if (viewModel.isWorkRunning) {
			viewModel.cancelWork()
		} else {
			findNavController().popBackStack()
		}
	}

	private fun CoroutineScope.observeViewModel() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			observeStatus()
			observeProgress()
			observeErrorMessages()
			observeButtonText()
			observeWorkSucceeded()
			observeWorkCanceled()
		}
	}

	private fun CoroutineScope.observeStatus() = launch {
		viewModel.status.collect {
			binding.textviewWorkStatus.text = it.resolve(requireContext())
		}
	}

	private fun CoroutineScope.observeProgress() = launch {
		viewModel.progressData.collect {
			binding.progressbarWork.progress = it.progress
			binding.progressbarWork.max = it.max
			binding.progressbarWork.isIndeterminate = it.isIndeterminate
		}
	}

	private fun CoroutineScope.observeErrorMessages() = launch {
		viewModel.errorMessage.collectLatest { error ->
			if (error == Message.empty) {
				return@collectLatest
			}
			val title = error.title.resolve(requireContext())
			val message = error.message.resolve(requireContext())
			AlertDialog.Builder(requireContext())
				.setCancelable(true)
				.setTitle(title)
				.setMessage(message)
				.setNeutralButton(R.string.dialog_button_copy_to_clipboard) { _, _ ->
					requireContext().copyTextToClipboard("Okkei Patcher Exception", message)
				}
				.setOnDismissListener {
					viewModel.closeErrorMessage()
					findNavController().popBackStack()
				}
				.show()
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
}