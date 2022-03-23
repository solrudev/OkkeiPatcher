package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface GetRestoreWorkUseCase {

	/**
	 * @return restore work and `null` if it has never been started yet.
	 */
	operator fun invoke(): Work?
}