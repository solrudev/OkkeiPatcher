package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.shouldShowErrorMessage
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@AndroidEntryPoint
class ClearDataFragment : DialogFragment(), FeatureView<ClearDataUiState> {

	private val viewModel by viewModels<ClearDataViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		startRender()
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: ClearDataUiState) {
		if (uiState.warning.shouldShow) {
			showWarning(uiState.warning.data)
		}
		if (uiState.isCleared) {
			showSuccessMessage()
			findNavController().popBackStack()
		}
		if (uiState.shouldShowErrorMessage) {
			showErrorMessage(uiState.error)
			findNavController().popBackStack()
		}
	}

	private fun startRender() {
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.collect(::render)
			}
		}
	}

	private fun showWarning(message: Message) {
		requireContext().createDialogBuilder(message)
			.setPositiveButton(R.string.button_text_clear) { _, _ ->
				viewModel.dispatchEvent(ClearingRequested)
			}
			.setNegativeButton(android.R.string.cancel) { _, _ ->
				findNavController().popBackStack()
			}
			.setOnCancelListener {
				findNavController().popBackStack()
			}
			.setOnDismissListener {
				viewModel.dispatchEvent(WarningDismissed)
			}
			.showWithLifecycle(lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(WarningShown)
	}

	private fun showSuccessMessage() {
		parentFragment?.view?.let {
			Snackbar.make(it, R.string.snackbar_data_cleared, Snackbar.LENGTH_SHORT).show()
		}
	}

	private fun showErrorMessage(error: LocalizedString) {
		val errorString = error.resolve(requireContext())
		parentFragment?.view?.let {
			Snackbar.make(it, errorString, Snackbar.LENGTH_SHORT).show()
		}
		viewModel.dispatchEvent(ErrorMessageShown)
	}
}