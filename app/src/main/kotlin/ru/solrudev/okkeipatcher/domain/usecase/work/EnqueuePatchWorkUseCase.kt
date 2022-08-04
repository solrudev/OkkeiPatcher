package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import javax.inject.Inject

interface EnqueuePatchWorkUseCase {

	/**
	 * Enqueues new patch work.
	 *
	 * @return patch [Work].
	 */
	suspend operator fun invoke(): Work
}

class EnqueuePatchWorkUseCaseImpl @Inject constructor(private val patchWorkRepository: PatchWorkRepository) :
	EnqueuePatchWorkUseCase {

	override suspend fun invoke() = patchWorkRepository.enqueueWork()
}