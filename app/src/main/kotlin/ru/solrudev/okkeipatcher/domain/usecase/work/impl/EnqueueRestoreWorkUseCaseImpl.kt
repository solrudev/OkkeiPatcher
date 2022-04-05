package ru.solrudev.okkeipatcher.domain.usecase.work.impl

import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.usecase.work.EnqueueRestoreWorkUseCase
import javax.inject.Inject

class EnqueueRestoreWorkUseCaseImpl @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) :
	EnqueueRestoreWorkUseCase {

	override suspend fun invoke() = restoreWorkRepository.enqueueRestoreWork()
}