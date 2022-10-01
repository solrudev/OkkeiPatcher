package ru.solrudev.okkeipatcher.ui.screen.permissions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.simpleinstaller.activityresult.InstallPermissionContract
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.ActivityPermissionsBinding
import ru.solrudev.okkeipatcher.domain.model.Permission
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.featureViewModels
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.PermissionStateChanged
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.allPermissionsGranted
import ru.solrudev.okkeipatcher.ui.util.onBackPressed

@AndroidEntryPoint
class PermissionsActivity : AppCompatActivity(R.layout.activity_permissions), FeatureView<PermissionsUiState> {

	private val binding by viewBinding(ActivityPermissionsBinding::bind, R.id.container_permissions)
	private val viewModel: PermissionsViewModel by featureViewModels()

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

	private val permissionsAdapter = PermissionsAdapter(::requestPermission)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		onBackPressed {
			finishAffinity()
		}
		binding.recyclerviewPermissions.adapter = permissionsAdapter
	}

	override fun render(uiState: PermissionsUiState) {
		if (uiState.allPermissionsGranted) {
			finish()
		}
		permissionsAdapter.submitList(uiState.permissions)
	}

	private fun requestPermission(permission: Permission) = when (permission) {
		Permission.Storage -> {
			val storagePermissions = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
			storagePermissionLauncher.launch(storagePermissions)
		}
		Permission.Install -> installPermissionLauncher.launch()
	}
}