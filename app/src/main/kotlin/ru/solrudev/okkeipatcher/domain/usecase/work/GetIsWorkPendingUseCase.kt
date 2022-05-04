package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

interface GetIsWorkPendingUseCase {
	suspend operator fun invoke(work: Work): Boolean
}

class GetIsWorkPendingUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetIsWorkPendingUseCase {

	override suspend fun invoke(work: Work) = workRepository.getIsPending(work)
}