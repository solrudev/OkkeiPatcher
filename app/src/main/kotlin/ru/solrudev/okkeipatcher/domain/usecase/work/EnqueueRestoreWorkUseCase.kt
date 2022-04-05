package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface EnqueueRestoreWorkUseCase {

	/**
	 * Enqueues new restore work.
	 *
	 * @return restore [Work].
	 */
	suspend operator fun invoke(): Work
}