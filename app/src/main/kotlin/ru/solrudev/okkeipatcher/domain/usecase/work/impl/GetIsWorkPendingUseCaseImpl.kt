package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.GetIsWorkPendingUseCase
import javax.inject.Inject

class GetIsWorkPendingUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetIsWorkPendingUseCase {

	override suspend fun invoke(work: Work) = workRepository.getIsPending(work)
}