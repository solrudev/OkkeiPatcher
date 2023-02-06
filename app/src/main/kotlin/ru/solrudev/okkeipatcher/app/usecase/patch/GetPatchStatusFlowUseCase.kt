package ru.solrudev.okkeipatcher.app.usecase.patch

import kotlinx.coroutines.flow.distinctUntilChanged
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import javax.inject.Inject

class GetPatchStatusFlowUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	operator fun invoke() = preferencesRepository.patchStatus.flow.distinctUntilChanged()
}