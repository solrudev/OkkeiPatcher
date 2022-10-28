package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.bindHeadless
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.shouldShowErrorMessage
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showSnackbar
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@AndroidEntryPoint
class ClearDataFragment : DialogFragment(), FeatureView<ClearDataUiState> {

	private val viewModel by viewModels<ClearDataViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.bindHeadless(this)
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

	private fun showWarning(message: Message) {
		requireContext().createDialogBuilder(message)
			.setIcon(R.drawable.ic_clear_data)
			.setPositiveButton(R.string.button_text_clear) { _, _ ->
				viewModel.dispatchEvent(ClearingRequested)
			}
			.setNegativeButton(R.string.button_text_cancel) { _, _ ->
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
		parentFragment?.view?.let { parentView ->
			showSnackbar(parentView, R.string.snackbar_data_cleared, Snackbar.LENGTH_SHORT)
		}
	}

	private fun showErrorMessage(error: LocalizedString) {
		parentFragment?.view?.let { parentView ->
			val errorString = error.resolve(requireContext())
			showSnackbar(parentView, errorString, Snackbar.LENGTH_SHORT)
		}
		viewModel.dispatchEvent(ErrorMessageShown)
	}
}