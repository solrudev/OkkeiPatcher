package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.model.Permission

interface PermissionsRepository {
	fun getRequiredPermissions(): Map<Permission, Boolean>
	fun isSaveDataAccessGranted(): Boolean
	fun isStoragePermissionGranted(): Boolean
	fun isInstallPermissionGranted(): Boolean
}