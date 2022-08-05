package ru.solrudev.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Inject

interface GetPendingWorkFlowUseCase {
	operator fun invoke(): Flow<Work>
}

class GetPendingWorkFlowUseCaseImpl @Inject constructor(
	private val patchWorkRepository: PatchWorkRepository,
	private val restoreWorkRepository: RestoreWorkRepository
) : GetPendingWorkFlowUseCase {

	override fun invoke() = merge(
		patchWorkRepository.getPendingWorkFlow(),
		restoreWorkRepository.getPendingWorkFlow()
	)
}