package ru.solrudev.okkeipatcher.ui.screen.permissions.model

import io.github.solrudev.jetmvi.UiState
import ru.solrudev.okkeipatcher.domain.model.Permission

data class PermissionsUiState(
	val permissions: List<PermissionUiState> = emptyList()
) : UiState

val PermissionsUiState.allPermissionsGranted: Boolean
	get() = permissions.all { it.isGranted }

data class PermissionUiState(
	val permission: Permission,
	val isGranted: Boolean
)