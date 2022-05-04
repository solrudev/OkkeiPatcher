package ru.solrudev.okkeipatcher.domain.usecase.work

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Inject

interface GetRestoreWorkUseCase {

	/**
	 * @return restore work and `null` if it has never been started yet.
	 */
	operator fun invoke(): Work?
}

class GetRestoreWorkUseCaseImpl @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) :
	GetRestoreWorkUseCase {

	override fun invoke() = restoreWorkRepository.getRestoreWork()
}