package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.WorkRepository
import solru.okkeipatcher.domain.usecase.work.GetIsWorkPendingUseCase
import javax.inject.Inject

class GetIsWorkPendingUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetIsWorkPendingUseCase {

	override suspend fun invoke(work: Work) = workRepository.getIsPending(work)
}