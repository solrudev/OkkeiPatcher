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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.ClearingRequested
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.ErrorMessageShown
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.WarningDismissed
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.WarningShown
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.shouldShowErrorMessage
import ru.solrudev.okkeipatcher.ui.main.util.showSnackbar
import ru.solrudev.okkeipatcher.ui.shared.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@AndroidEntryPoint
class ClearDataFragment : DialogFragment(), JetView<ClearDataUiState> {

	private val viewModel: ClearDataViewModel by jetViewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_FRAME, theme)
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
			.showWithLifecycle(lifecycle)
		viewModel.dispatchEvent(WarningShown)
	}

	private fun showSuccessMessage() {
		showSnackbar(R.string.snackbar_data_cleared, Snackbar.LENGTH_SHORT)
	}

	private fun showErrorMessage(error: LocalizedString) {
		val errorString = error.resolve(requireContext())
		showSnackbar(errorString, Snackbar.LENGTH_SHORT)
		viewModel.dispatchEvent(ErrorMessageShown)
	}
}