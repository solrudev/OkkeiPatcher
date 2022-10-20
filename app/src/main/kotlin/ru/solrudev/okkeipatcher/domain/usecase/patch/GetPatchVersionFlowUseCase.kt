package ru.solrudev.okkeipatcher.domain.usecase.patch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetPatchVersionFlowUseCase {
	operator fun invoke(): Flow<String>
}

class GetPatchVersionFlowUseCaseImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository
) : GetPatchVersionFlowUseCase {

	override fun invoke() = preferencesRepository.patchVersion.flow.distinctUntilChanged()
}