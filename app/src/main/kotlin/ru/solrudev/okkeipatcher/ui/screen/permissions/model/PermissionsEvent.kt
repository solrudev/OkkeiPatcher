package ru.solrudev.okkeipatcher.ui.screen.permissions.model

import io.github.solrudev.jetmvi.Event
import ru.solrudev.okkeipatcher.domain.model.Permission

sealed interface PermissionsEvent : Event {
	data class RequiredPermissionsLoaded(val permissions: Map<Permission, Boolean>) : PermissionsEvent
	data class PermissionStateChanged(val permission: Permission, val isGranted: Boolean) : PermissionsEvent
}