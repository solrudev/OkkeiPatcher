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

package ru.solrudev.okkeipatcher.ui.screen.permissions.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.PermissionStateChanged
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.RequiredPermissionsLoaded
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import javax.inject.Inject

class PermissionsReducer @Inject constructor() : Reducer<PermissionsEvent, PermissionsUiState> {

	override fun reduce(event: PermissionsEvent, state: PermissionsUiState) = when (event) {
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