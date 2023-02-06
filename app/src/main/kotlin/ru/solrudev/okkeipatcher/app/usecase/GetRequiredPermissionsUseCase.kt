package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.PermissionsRepository
import javax.inject.Inject

class GetRequiredPermissionsUseCase @Inject constructor(private val permissionsRepository: PermissionsRepository) {
	operator fun invoke() = permissionsRepository.getRequiredPermissions()
}