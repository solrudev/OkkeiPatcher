package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.work.WorkRepository
import solru.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import javax.inject.Inject

class CancelWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) : CancelWorkUseCase {
	override fun invoke(work: Work) = workRepository.cancelWork(work)
}