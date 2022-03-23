package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface CompleteWorkUseCase {

	/**
	 * Sets `isPending` for the work to `false`.
	 */
	suspend operator fun invoke(work: Work)
}