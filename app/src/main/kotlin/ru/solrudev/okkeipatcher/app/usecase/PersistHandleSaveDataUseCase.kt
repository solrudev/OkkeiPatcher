package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import javax.inject.Inject

class PersistHandleSaveDataUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	suspend operator fun invoke(value: Boolean) = preferencesRepository.handleSaveData.persist(value)
}