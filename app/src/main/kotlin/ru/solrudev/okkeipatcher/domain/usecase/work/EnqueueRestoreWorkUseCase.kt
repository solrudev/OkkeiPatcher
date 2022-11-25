package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Inject

class EnqueueRestoreWorkUseCase @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) {
	suspend operator fun invoke() = restoreWorkRepository.enqueueWork()
}