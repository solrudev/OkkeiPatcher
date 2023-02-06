package ru.solrudev.okkeipatcher.app.usecase.work

import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import javax.inject.Inject

/**
 * Sets `isPending` for the work to `false`.
 */
class CompleteWorkUseCase @Inject constructor(private val workRepository: WorkRepository) {
	suspend operator fun invoke(work: Work) = workRepository.updateIsPending(work.id, isPending = false)
}