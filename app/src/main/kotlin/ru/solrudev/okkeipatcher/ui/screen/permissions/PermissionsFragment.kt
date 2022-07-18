package ru.solrudev.okkeipatcher.ui.screen.permissions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.simpleinstaller.activityresult.InstallPermissionContract
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentPermissionsBinding
import ru.solrudev.okkeipatcher.domain.model.Permission
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.renderBy
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.PermissionStateChanged
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.allPermissionsGranted
import ru.solrudev.okkeipatcher.ui.util.onBackPressed
import ru.solrudev.okkeipatcher.ui.util.prepareOptionsMenu
import ru.solrudev.okkeipatcher.ui.util.setupTransitions

@AndroidEntryPoint
class PermissionsFragment : Fragment(R.layout.fragment_permissions), FeatureView<PermissionsUiState> {

	private val binding by viewBinding(FragmentPermissionsBinding::bind)
	private val viewModel by viewModels<PermissionsViewModel>()

	private val storagePermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { storagePermissions ->
		val isGranted = storagePermissions.all { (_, isGranted) -> isGranted }
		if (isGranted) {
			viewModel.dispatchEvent(
				PermissionStateChanged(Permission.Storage, isGranted = true)
			)
		}
	}

	private val installPermissionLauncher = registerForActivityResult(
		InstallPermissionContract()
	) { isGranted ->
		if (isGranted) {
			viewModel.dispatchEvent(
				PermissionStateChanged(Permission.Install, isGranted = true)
			)
		}
	}

	private val permissionsAdapter = PermissionsAdapter {
		requestPermission(it.permission)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupTransitions()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		onBackPressed {
			requireActivity().finish()
		}
		prepareOptionsMenu {
			clear()
		}
		binding.recyclerViewPermissions.adapter = permissionsAdapter
		viewModel.renderBy(this)
	}

	override fun render(uiState: PermissionsUiState) {
		if (uiState.allPermissionsGranted) {
			findNavController().popBackStack()
		}
		permissionsAdapter.submitList(uiState.permissions)
	}

	private fun requestPermission(permission: Permission) = when (permission) {
		Permission.Storage -> {
			val storagePermissions = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
			storagePermissionLauncher.launch(storagePermissions)
		}
		Permission.Install -> installPermissionLauncher.launch(Unit)
	}
}