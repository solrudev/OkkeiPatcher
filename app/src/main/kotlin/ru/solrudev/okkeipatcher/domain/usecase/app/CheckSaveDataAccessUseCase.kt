package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface CheckSaveDataAccessUseCase {
	suspend operator fun invoke()
}

class CheckSaveDataAccessUseCaseImpl @Inject constructor(
	private val permissionsRepository: PermissionsRepository,
	private val preferencesRepository: PreferencesRepository
) : CheckSaveDataAccessUseCase {

	override suspend fun invoke() {
		val isSaveDataAccessGranted = permissionsRepository.isSaveDataAccessGranted()
		val currentHandleSaveData = preferencesRepository.handleSaveData.retrieve()
		preferencesRepository.handleSaveData.persist(currentHandleSaveData && isSaveDataAccessGranted)
	}
}