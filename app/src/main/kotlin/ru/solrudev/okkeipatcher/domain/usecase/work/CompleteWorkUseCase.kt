package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

interface CompleteWorkUseCase {

	/**
	 * Sets `isPending` for the work to `false`.
	 */
	suspend operator fun invoke(work: Work)
}

class CompleteWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	CompleteWorkUseCase {

	override suspend fun invoke(work: Work) = workRepository.updateIsPending(work, isPending = false)
}