package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface EnqueuePatchWorkUseCase {

	/**
	 * Enqueues new patch work.
	 *
	 * @return patch [Work].
	 */
	suspend operator fun invoke(): Work
}