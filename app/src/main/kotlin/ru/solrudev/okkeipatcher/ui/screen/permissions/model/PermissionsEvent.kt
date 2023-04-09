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

package ru.solrudev.okkeipatcher.ui.screen.permissions.model

import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.Permission

sealed interface PermissionsEvent : JetEvent {
	data class RequiredPermissionsLoaded(val permissions: Map<Permission, Boolean>) : PermissionsEvent
	data class PermissionStateChanged(val permission: Permission, val isGranted: Boolean) : PermissionsEvent
}