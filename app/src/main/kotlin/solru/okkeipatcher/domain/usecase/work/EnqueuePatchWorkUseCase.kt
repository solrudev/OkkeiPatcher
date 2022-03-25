package solru.okkeipatcher.domain.usecase.work

import solru.okkeipatcher.domain.model.Work

interface EnqueuePatchWorkUseCase {

	/**
	 * Enqueues new patch work.
	 *
	 * @return patch [Work].
	 */
	suspend operator fun invoke(): Work
}