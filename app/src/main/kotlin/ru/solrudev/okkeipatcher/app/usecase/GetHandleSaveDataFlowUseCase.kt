package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import javax.inject.Inject

class GetHandleSaveDataFlowUseCase @Inject constructor(
	private val preferencesRepository: PreferencesRepository
) {

	operator fun invoke() = preferencesRepository.handleSaveData.flow
}