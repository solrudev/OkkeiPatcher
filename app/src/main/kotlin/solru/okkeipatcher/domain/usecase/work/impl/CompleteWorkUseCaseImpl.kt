package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.work.WorkRepository
import solru.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import javax.inject.Inject

class CompleteWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	CompleteWorkUseCase {

	override suspend fun invoke(work: Work) = workRepository.updateIsPending(work, isPending = false)
}