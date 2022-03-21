package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface StartRestoreWorkUseCase {

	/**
	 * Starts new restore work.
	 *
	 * @return restore [Work].
	 */
	operator fun invoke(): Work
}