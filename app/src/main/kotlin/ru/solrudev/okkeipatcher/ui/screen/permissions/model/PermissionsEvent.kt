package ru.solrudev.okkeipatcher.ui.screen.permissions.model

import ru.solrudev.okkeipatcher.domain.model.Permission
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface PermissionsEvent : Event {
	data class RequiredPermissionsLoaded(val permissions: Map<Permission, Boolean>) : PermissionsEvent
	data class PermissionStateChanged(val permission: Permission, val isGranted: Boolean) : PermissionsEvent
}