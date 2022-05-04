package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Inject

interface EnqueueRestoreWorkUseCase {

	/**
	 * Enqueues new restore work.
	 *
	 * @return restore [Work].
	 */
	suspend operator fun invoke(): Work
}

class EnqueueRestoreWorkUseCaseImpl @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) :
	EnqueueRestoreWorkUseCase {

	override suspend fun invoke() = restoreWorkRepository.enqueueRestoreWork()
}