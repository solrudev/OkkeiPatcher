package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface GetRestoreWorkUseCase {

	/**
	 * @return restore work and `null` if it has never been started yet.
	 */
	operator fun invoke(): Work?
}