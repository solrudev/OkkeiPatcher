package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface EnqueueRestoreWorkUseCase {

	/**
	 * Enqueues new restore work.
	 *
	 * @return restore [Work].
	 */
	suspend operator fun invoke(): Work
}