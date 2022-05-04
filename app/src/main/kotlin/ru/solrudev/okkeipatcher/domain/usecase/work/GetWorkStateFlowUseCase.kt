package ru.solrudev.okkeipatcher.domain.usecase.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.WorkState
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

interface GetWorkStateFlowUseCase {
	operator fun invoke(work: Work): Flow<WorkState>
}

class GetWorkStateFlowUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetWorkStateFlowUseCase {

	override fun invoke(work: Work) = workRepository.getWorkStateFlow(work)
}