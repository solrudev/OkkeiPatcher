package ru.solrudev.okkeipatcher.app.usecase.work

import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import javax.inject.Inject

class CancelWorkUseCase @Inject constructor(private val workRepository: WorkRepository) {
	operator fun invoke(work: Work) = workRepository.cancelWork(work.id)
}