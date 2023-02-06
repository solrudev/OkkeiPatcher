package ru.solrudev.okkeipatcher.app.repository

import ru.solrudev.okkeipatcher.app.model.Permission

interface PermissionsRepository {
	fun getRequiredPermissions(): Map<Permission, Boolean>
	fun isSaveDataAccessGranted(): Boolean
	fun isStoragePermissionGranted(): Boolean
	fun isInstallPermissionGranted(): Boolean
}