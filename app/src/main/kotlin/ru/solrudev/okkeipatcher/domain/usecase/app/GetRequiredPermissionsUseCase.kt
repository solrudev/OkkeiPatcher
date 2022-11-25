package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import javax.inject.Inject

class GetRequiredPermissionsUseCase @Inject constructor(private val permissionsRepository: PermissionsRepository) {
	operator fun invoke() = permissionsRepository.getRequiredPermissions()
}