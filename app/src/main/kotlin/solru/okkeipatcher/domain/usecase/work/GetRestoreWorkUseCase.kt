package solru.okkeipatcher.domain.usecase.work

import solru.okkeipatcher.domain.model.Work

interface GetRestoreWorkUseCase {

	/**
	 * @return restore work and `null` if it has never been started yet.
	 */
	operator fun invoke(): Work?
}