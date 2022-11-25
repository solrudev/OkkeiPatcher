package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

class CancelWorkUseCase @Inject constructor(private val workRepository: WorkRepository) {
	operator fun invoke(work: Work) = workRepository.cancelWork(work.id)
}