package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

class GetHandleSaveDataFlowUseCase @Inject constructor(
	private val preferencesRepository: PreferencesRepository
) {

	operator fun invoke() = preferencesRepository.handleSaveData.flow
}