package solru.okkeipatcher.domain.usecase.work

import solru.okkeipatcher.domain.model.Work

interface EnqueueRestoreWorkUseCase {

	/**
	 * Enqueues new restore work.
	 *
	 * @return restore [Work].
	 */
	suspend operator fun invoke(): Work
}