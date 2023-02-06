package ru.solrudev.okkeipatcher.ui.screen.permissions.model

import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.Permission

sealed interface PermissionsEvent : JetEvent {
	data class RequiredPermissionsLoaded(val permissions: Map<Permission, Boolean>) : PermissionsEvent
	data class PermissionStateChanged(val permission: Permission, val isGranted: Boolean) : PermissionsEvent
}