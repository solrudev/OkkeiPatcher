package ru.solrudev.okkeipatcher.domain.usecase.patch

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

interface GetPatchVersionFlowUseCase {
	suspend operator fun invoke(): Flow<String>
}

class GetPatchVersionFlowUseCaseImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository
) : GetPatchVersionFlowUseCase {

	override suspend fun invoke() = preferencesRepository.patchVersion.flow
}