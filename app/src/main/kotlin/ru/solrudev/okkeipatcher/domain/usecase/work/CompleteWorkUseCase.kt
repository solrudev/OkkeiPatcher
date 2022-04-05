package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work

interface CompleteWorkUseCase {

	/**
	 * Sets `isPending` for the work to `false`.
	 */
	suspend operator fun invoke(work: Work)
}