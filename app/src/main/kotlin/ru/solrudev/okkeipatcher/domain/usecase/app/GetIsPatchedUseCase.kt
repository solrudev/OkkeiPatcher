package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetIsPatchedUseCase {
	suspend operator fun invoke(): Boolean
}

class GetIsPatchedUseCaseImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) :
	GetIsPatchedUseCase {

	override suspend fun invoke() = preferencesRepository.isPatchedDao.retrieve()
}