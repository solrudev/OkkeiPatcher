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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.PermissionGranted
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.RationaleDismissed
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.RationaleShown
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.shared.model.shouldShow
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SaveDataAccessFragment : DialogFragment(), JetView<SaveDataAccessUiState> {

	private val viewModel: SaveDataAccessViewModel by jetViewModels()

	// Needs context to be initialized.
	private lateinit var permissionRequestLauncher: ActivityResultLauncher<Unit>

	override fun onAttach(context: Context) {
		super.onAttach(context)
		permissionRequestLauncher = createPermissionRequestLauncher(context)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_FRAME, theme)
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	override fun render(uiState: SaveDataAccessUiState) {
		if (uiState.rationale.shouldShow) {
			showRationale(uiState.rationale.data)
		}
		if (uiState.handleSaveDataEnabled) {
			findNavController().popBackStack()
		}
	}

	private fun createPermissionRequestLauncher(context: Context): ActivityResultLauncher<Unit> {
		return registerForActivityResult(AndroidDataAccessContract(context.applicationContext)) { isGranted ->
			if (isGranted) {
				viewModel.dispatchEvent(PermissionGranted)
			} else {
				findNavController().popBackStack()
			}
		}
	}

	private fun showRationale(message: Message) {
		requireContext().createDialogBuilder(message)
			.setIcon(R.drawable.ic_save_data)
			.setPositiveButton(R.string.button_text_grant) { _, _ ->
				permissionRequestLauncher.launch()
			}
			.setNegativeButton(R.string.button_text_cancel) { _, _ ->
				findNavController().popBackStack()
			}
			.setOnCancelListener {
				findNavController().popBackStack()
			}
			.setOnDismissListener {
				viewModel.dispatchEvent(RationaleDismissed)
			}
			.showWithLifecycle(lifecycle)
		viewModel.dispatchEvent(RationaleShown)
	}
}