package ru.solrudev.okkeipatcher.domain.usecase.app.impl

import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsPatchedUseCase
import javax.inject.Inject

class GetIsPatchedUseCaseImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) :
	GetIsPatchedUseCase {

	override suspend fun invoke() = preferencesRepository.getIsPatched()
}