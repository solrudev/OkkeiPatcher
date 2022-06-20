package ru.solrudev.okkeipatcher.domain.usecase.app

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetPatchStatusFlowUseCase {
	suspend operator fun invoke(): Flow<Boolean>
}

class GetPatchStatusFlowUseCaseImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) :
	GetPatchStatusFlowUseCase {

	override suspend fun invoke() = preferencesRepository.patchStatus.flow
}