package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.FeatureView
import io.github.solrudev.jetmvi.bindHeadless
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SaveDataAccessFragment : DialogFragment(), FeatureView<SaveDataAccessUiState> {

	private val viewModel by viewModels<SaveDataAccessViewModel>()

	// Needs context to be initialized.
	private lateinit var permissionRequestLauncher: ActivityResultLauncher<Unit>

	override fun onAttach(context: Context) {
		super.onAttach(context)
		permissionRequestLauncher = createPermissionRequestLauncher(context)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_FRAME, theme)
		viewModel.bindHeadless(this)
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
			.showWithLifecycle(lifecycle, Lifecycle.Event.ON_STOP)
		viewModel.dispatchEvent(RationaleShown)
	}
}