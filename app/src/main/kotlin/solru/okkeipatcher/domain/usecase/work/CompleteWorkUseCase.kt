package solru.okkeipatcher.domain.usecase.work

import solru.okkeipatcher.domain.model.Work

interface CompleteWorkUseCase {

	/**
	 * Sets `isPending` for the work to `false`.
	 */
	suspend operator fun invoke(work: Work)
}