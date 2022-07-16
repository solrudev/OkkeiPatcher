package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import javax.inject.Inject

interface GetIsSaveDataAccessGrantedUseCase {
	operator fun invoke(): Boolean
}

class GetIsSaveDataAccessGrantedUseCaseImpl @Inject constructor(
	private val permissionsRepository: PermissionsRepository
) : GetIsSaveDataAccessGrantedUseCase {

	override fun invoke() = permissionsRepository.isSaveDataAccessGranted()
}