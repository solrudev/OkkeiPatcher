package ru.solrudev.okkeipatcher.domain.repository.app

interface PermissionsRepository {
	fun isSaveDataAccessGranted(): Boolean
}