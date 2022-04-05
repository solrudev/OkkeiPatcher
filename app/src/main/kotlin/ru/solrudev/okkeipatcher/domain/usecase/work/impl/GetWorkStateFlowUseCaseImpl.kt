package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.GetWorkStateFlowUseCase
import javax.inject.Inject

class GetWorkStateFlowUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetWorkStateFlowUseCase {

	override fun invoke(work: Work) = workRepository.getWorkStateFlow(work)
}