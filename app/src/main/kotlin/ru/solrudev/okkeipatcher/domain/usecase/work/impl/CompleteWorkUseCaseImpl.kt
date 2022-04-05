package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import javax.inject.Inject

class CompleteWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	CompleteWorkUseCase {

	override suspend fun invoke(work: Work) = workRepository.updateIsPending(work, isPending = false)
}