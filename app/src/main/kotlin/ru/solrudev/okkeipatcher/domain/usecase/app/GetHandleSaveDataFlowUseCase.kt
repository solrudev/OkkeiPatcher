package ru.solrudev.okkeipatcher.domain.usecase.app

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetHandleSaveDataFlowUseCase {
	operator fun invoke(): Flow<Boolean>
}

class GetHandleSaveDataFlowUseCaseImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository
) : GetHandleSaveDataFlowUseCase {

	override fun invoke() = preferencesRepository.handleSaveData.flow
}