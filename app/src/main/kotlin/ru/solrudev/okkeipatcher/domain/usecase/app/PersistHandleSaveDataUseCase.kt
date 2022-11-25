package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

class PersistHandleSaveDataUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	suspend operator fun invoke(value: Boolean) = preferencesRepository.handleSaveData.persist(value)
}