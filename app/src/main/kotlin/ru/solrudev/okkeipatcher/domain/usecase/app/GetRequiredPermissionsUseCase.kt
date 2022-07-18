package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.Permission
import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import javax.inject.Inject

interface GetRequiredPermissionsUseCase {
	operator fun invoke(): Map<Permission, Boolean>
}

class GetRequiredPermissionsUseCaseImpl @Inject constructor(
	private val permissionsRepository: PermissionsRepository
) : GetRequiredPermissionsUseCase {

	override fun invoke() = permissionsRepository.getRequiredPermissions()
}