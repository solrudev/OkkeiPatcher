/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.ui.screen.permissions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.Permission
import ru.solrudev.okkeipatcher.databinding.FragmentPermissionsBinding
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.PermissionStateChanged
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.allPermissionsGranted
import ru.solrudev.okkeipatcher.ui.util.onBackPressed
import ru.solrudev.okkeipatcher.ui.util.requireNavHostActivity

@AndroidEntryPoint
class PermissionsFragment : Fragment(R.layout.fragment_permissions), JetView<PermissionsUiState> {

	private val binding by viewBinding(FragmentPermissionsBinding::bind)
	private val viewModel: PermissionsViewModel by jetViewModels()

	private val storagePermissionLauncher = registerForActivityResult(RequestMultiplePermissions()) { permissions ->
		val isGranted = permissions.all { (_, isGranted) -> isGranted }
		if (isGranted) {
			viewModel.dispatchEvent(PermissionStateChanged(Permission.Storage, isGranted = true))
		}
	}

	// Needs context to be initialized.
	@TargetApi(Build.VERSION_CODES.O)
	private var installPermissionLauncher: ActivityResultLauncher<Unit>? = null

	private val permissionsAdapter = PermissionsAdapter(::requestPermission)

	override fun onAttach(context: Context) {
		super.onAttach(context)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			installPermissionLauncher = createInstallPermissionLauncher(context)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		onBackPressed {
			requireActivity().finish()
		}
		binding.recyclerviewPermissions.adapter = permissionsAdapter
	}

	override fun render(uiState: PermissionsUiState) {
		if (uiState.allPermissionsGranted) {
			requireNavHostActivity().notifyAllPermissionsGranted()
			findNavController().popBackStack()
		}
		permissionsAdapter.submitList(uiState.permissions)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createInstallPermissionLauncher(context: Context): ActivityResultLauncher<Unit> {
		return registerForActivityResult(InstallPermissionContract(context.packageManager)) { isGranted ->
			if (isGranted) {
				viewModel.dispatchEvent(PermissionStateChanged(Permission.Install, isGranted = true))
			}
		}
	}

	private fun requestPermission(permission: Permission) = when (permission) {
		Permission.Storage -> {
			val storagePermissions = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
			storagePermissionLauncher.launch(storagePermissions)
		}

		Permission.Install -> installPermissionLauncher?.launch()
	}
}