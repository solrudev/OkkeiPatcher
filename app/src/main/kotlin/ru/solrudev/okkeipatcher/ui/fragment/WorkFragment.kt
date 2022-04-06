package ru.solrudev.okkeipatcher.ui.fragment

import android.app.NotificationManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentWorkBinding
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.ui.util.extension.copyTextToClipboard
import ru.solrudev.okkeipatcher.ui.util.extension.setupTransitions
import ru.solrudev.okkeipatcher.ui.util.extension.showWithLifecycle
import ru.solrudev.okkeipatcher.ui.viewmodel.WorkViewModel

abstract class WorkFragment<VM : WorkViewModel> : Fragment(R.layout.fragment_work) {

	protected abstract val viewModel: VM
	private val binding by viewBinding(FragmentWorkBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		setupTransitions()
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

	private fun setupNavigation() {
		binding.buttonWork.setOnClickListener {
			viewModel.requestWorkCancel()
		}
	}

	private fun CoroutineScope.observeUiState() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			viewModel.uiState.collect { uiState ->
				binding.progressbarWorkLoading.isVisible = uiState.isLoading
				binding.buttonWork.isEnabled = uiState.isButtonEnabled
				if (uiState.isWorkSuccessful) {
					onWorkSuccess()
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

	private fun clearNotifications() {
		val notificationManager = requireContext().getSystemService<NotificationManager>()
		notificationManager?.cancelAll()
	}

	private fun onWorkSuccess() {
		binding.buttonWork.setOnClickListener {
			findNavController().popBackStack()
		}
		binding.buttonWork.setText(android.R.string.ok)
		clearNotifications()
	}

	private fun setProgress(progressData: ProgressData) {
		binding.progressbarWork.max = progressData.max
		binding.progressbarWork.setProgressCompat(progressData.progress, true)
		val percentDone = (progressData.progress.toDouble() / progressData.max * 100).toInt()
		binding.textviewWorkPercentDone.text = getString(R.string.percent_done, percentDone)
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
		clearNotifications()
	}

	private fun createDialogBuilder(message: Message): MaterialAlertDialogBuilder {
		val titleString = message.title.resolve(requireContext())
		val messageString = message.message.resolve(requireContext())
		return MaterialAlertDialogBuilder(requireContext())
			.setCancelable(true)
			.setTitle(titleString)
			.setMessage(messageString)
	}
}