package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface EnqueuePatchWorkUseCase {

	/**
	 * Enqueues new patch work.
	 *
	 * @return patch [Work].
	 */
	suspend operator fun invoke(): Work
}