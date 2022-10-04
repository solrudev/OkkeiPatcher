package ru.solrudev.okkeipatcher.domain.usecase.patch

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetPatchStatusFlowUseCase {
	operator fun invoke(): Flow<Boolean>
}

class GetPatchStatusFlowUseCaseImpl @Inject constructor(private val preferencesRepository: PreferencesRepository) :
	GetPatchStatusFlowUseCase {

	override fun invoke() = preferencesRepository.patchStatus.flow
}