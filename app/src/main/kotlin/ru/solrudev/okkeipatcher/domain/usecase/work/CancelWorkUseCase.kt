package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

interface CancelWorkUseCase {
	operator fun invoke(work: Work)
}

class CancelWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) : CancelWorkUseCase {
	override fun invoke(work: Work) = workRepository.cancelWork(work.id)
}