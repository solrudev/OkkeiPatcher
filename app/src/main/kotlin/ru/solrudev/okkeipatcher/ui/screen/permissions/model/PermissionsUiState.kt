package ru.solrudev.okkeipatcher.ui.screen.permissions.model

import io.github.solrudev.jetmvi.JetState
import ru.solrudev.okkeipatcher.app.model.Permission

data class PermissionsUiState(
	val permissions: List<PermissionUiState> = emptyList()
) : JetState

val PermissionsUiState.allPermissionsGranted: Boolean
	get() = permissions.all { it.isGranted }

data class PermissionUiState(
	val permission: Permission,
	val isGranted: Boolean
)