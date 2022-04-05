package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.work.WorkRepository
import solru.okkeipatcher.domain.usecase.work.GetWorkStateFlowUseCase
import javax.inject.Inject

class GetWorkStateFlowUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetWorkStateFlowUseCase {

	override fun invoke(work: Work) = workRepository.getWorkStateFlow(work)
}