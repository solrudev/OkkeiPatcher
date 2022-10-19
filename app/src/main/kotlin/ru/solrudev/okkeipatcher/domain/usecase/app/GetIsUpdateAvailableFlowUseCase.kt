package ru.solrudev.okkeipatcher.domain.usecase.app

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface GetIsUpdateAvailableFlowUseCase {
	operator fun invoke(): Flow<Boolean>
}

class GetIsUpdateAvailableFlowUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : GetIsUpdateAvailableFlowUseCase {

	override fun invoke() = okkeiPatcherRepository.isUpdateAvailable
}