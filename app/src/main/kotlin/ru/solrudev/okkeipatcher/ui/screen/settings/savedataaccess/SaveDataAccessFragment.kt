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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.model.shouldShow
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.util.createDialogBuilder
import ru.solrudev.okkeipatcher.ui.util.showWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SaveDataAccessFragment : DialogFragment(), FeatureView<SaveDataAccessUiState> {

	private val viewModel by viewModels<SaveDataAccessViewModel>()
	private lateinit var permissionRequestLauncher: ActivityResultLauncher<Unit>

	override fun onAttach(context: Context) {
		super.onAttach(context)
		permissionRequestLauncher = createPermissionRequestLauncher(context)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		startRender()
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

	private fun startRender() {
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.collect(::render)
			}
		}
	}

	private fun createPermissionRequestLauncher(context: Context): ActivityResultLauncher<Unit> {
		val contract = AndroidDataAccessContract(context.applicationContext)
		return registerForActivityResult(contract) { granted ->
			if (granted) {
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