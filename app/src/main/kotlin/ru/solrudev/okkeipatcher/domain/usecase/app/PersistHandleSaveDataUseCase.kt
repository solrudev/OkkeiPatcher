package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface PersistHandleSaveDataUseCase {
	suspend operator fun invoke(value: Boolean)
}

class PersistHandleSaveDataUseCaseImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository
) : PersistHandleSaveDataUseCase {

	override suspend fun invoke(value: Boolean) = preferencesRepository.handleSaveData.persist(value)
}