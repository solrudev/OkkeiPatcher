package ru.solrudev.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.merge
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Inject

class GetPendingWorkFlowUseCase @Inject constructor(
	private val patchWorkRepository: PatchWorkRepository,
	private val restoreWorkRepository: RestoreWorkRepository
) {

	operator fun invoke() = merge(
		patchWorkRepository.getPendingWorkFlow(),
		restoreWorkRepository.getPendingWorkFlow()
	)
}