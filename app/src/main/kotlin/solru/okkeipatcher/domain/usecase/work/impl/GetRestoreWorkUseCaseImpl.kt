package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.domain.repository.work.RestoreWorkRepository
import solru.okkeipatcher.domain.usecase.work.GetRestoreWorkUseCase
import javax.inject.Inject

class GetRestoreWorkUseCaseImpl @Inject constructor(private val restoreWorkRepository: RestoreWorkRepository) :
	GetRestoreWorkUseCase {

	override fun invoke() = restoreWorkRepository.getRestoreWork()
}