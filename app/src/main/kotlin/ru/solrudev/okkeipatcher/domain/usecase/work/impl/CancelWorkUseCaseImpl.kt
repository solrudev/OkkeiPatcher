package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import javax.inject.Inject

class CancelWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) : CancelWorkUseCase {
	override fun invoke(work: Work) = workRepository.cancelWork(work)
}