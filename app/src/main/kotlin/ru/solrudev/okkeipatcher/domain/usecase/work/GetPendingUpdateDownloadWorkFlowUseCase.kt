package ru.solrudev.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

interface GetPendingUpdateDownloadWorkFlowUseCase {
	operator fun invoke(): Flow<Work>
}

class GetPendingUpdateDownloadWorkFlowUseCaseImpl @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : GetPendingUpdateDownloadWorkFlowUseCase {

	override fun invoke() = okkeiPatcherRepository.getPendingUpdateDownloadWorkFlow()
}