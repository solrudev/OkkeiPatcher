package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.Work

interface GetPatchWorkUseCase {

	/**
	 * @return patch work and `null` if it has never been started yet.
	 */
	operator fun invoke(): Work?
}