package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.PermissionsRepository
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
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