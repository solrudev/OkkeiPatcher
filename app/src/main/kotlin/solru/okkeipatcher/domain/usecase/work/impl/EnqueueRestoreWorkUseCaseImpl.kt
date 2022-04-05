package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.repository.work.RestoreWorkRepository
import solru.okkeipatcher.domain.usecase.work.EnqueueRestoreWorkUseCase
import javax.inject.Inject

class EnqueueRestoreWorkUseCaseImpl @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) :
	EnqueueRestoreWorkUseCase {

	override suspend fun invoke() = restoreWorkRepository.enqueueRestoreWork()
}