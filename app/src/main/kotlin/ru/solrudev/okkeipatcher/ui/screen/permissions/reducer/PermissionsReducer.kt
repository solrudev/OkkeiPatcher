package ru.solrudev.okkeipatcher.ui.screen.permissions.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.PermissionStateChanged
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.RequiredPermissionsLoaded
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import javax.inject.Inject

class PermissionsReducer @Inject constructor() : Reducer<PermissionsUiState, PermissionsEvent> {

	override fun reduce(state: PermissionsUiState, event: PermissionsEvent) = when (event) {
		is PermissionStateChanged -> {
			val permissions = state.permissions.map {
				if (it.permission == event.permission) it.copy(isGranted = event.isGranted) else it
			}
			state.copy(permissions = permissions)
		}
		is RequiredPermissionsLoaded -> {
			val permissions = event.permissions.map { (permission, isGranted) ->
				PermissionUiState(permission, isGranted)
			}
			state.copy(permissions = permissions)
		}
	}
}