package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

class CheckSaveDataAccessUseCase @Inject constructor(
	private val permissionsRepository: PermissionsRepository,
	private val preferencesRepository: PreferencesRepository
) {

	suspend operator fun invoke() {
		val isSaveDataAccessGranted = permissionsRepository.isSaveDataAccessGranted()
		val currentHandleSaveData = preferencesRepository.handleSaveData.retrieve()
		preferencesRepository.handleSaveData.persist(currentHandleSaveData && isSaveDataAccessGranted)
	}
}