package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface StartPatchWorkUseCase {

	/**
	 * Starts new patch work.
	 *
	 * @return patch [Work].
	 */
	operator fun invoke(): Work
}