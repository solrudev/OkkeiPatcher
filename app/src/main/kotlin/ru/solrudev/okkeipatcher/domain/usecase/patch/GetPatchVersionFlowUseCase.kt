package ru.solrudev.okkeipatcher.domain.usecase.patch

import kotlinx.coroutines.flow.distinctUntilChanged
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

class GetPatchVersionFlowUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	operator fun invoke() = preferencesRepository.patchVersion.flow.distinctUntilChanged()
}