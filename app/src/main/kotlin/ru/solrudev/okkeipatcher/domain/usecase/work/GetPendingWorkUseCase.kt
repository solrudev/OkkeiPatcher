package ru.solrudev.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.first
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.WorkState
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

interface GetPendingWorkUseCase {
	suspend operator fun invoke(): Work?
}

class GetPendingWorkUseCaseImpl @Inject constructor(
	private val workRepository: WorkRepository,
	private val patchWorkRepository: PatchWorkRepository,
	private val restoreWorkRepository: RestoreWorkRepository
) : GetPendingWorkUseCase {

	override suspend fun invoke(): Work? {
		val patchWork = patchWorkRepository.getWork()
		val restoreWork = restoreWorkRepository.getWork()
		return pendingWorkOrNull(patchWork) ?: pendingWorkOrNull(restoreWork)
	}

	/**
	 * Returns [work] if it's pending, `null` otherwise.
	 */
	private suspend inline fun pendingWorkOrNull(work: Work?): Work? {
		work ?: return null
		val workState = workRepository
			.getWorkStateFlow(work)
			.first()
		if (workState is WorkState.Canceled) {
			workRepository.updateIsPending(work, isPending = false)
			return null
		}
		return if (workRepository.getIsPending(work)) work else null
	}
}