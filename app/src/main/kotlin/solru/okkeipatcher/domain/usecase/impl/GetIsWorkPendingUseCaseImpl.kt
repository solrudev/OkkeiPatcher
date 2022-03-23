package solru.okkeipatcher.domain.usecase.impl

import solru.okkeipatcher.data.Work
import solru.okkeipatcher.domain.usecase.GetIsWorkPendingUseCase
import solru.okkeipatcher.repository.WorkRepository
import javax.inject.Inject

class GetIsWorkPendingUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	GetIsWorkPendingUseCase {

	override suspend fun invoke(work: Work): Boolean {
		val workEntity = workRepository.getByWorkId(work.id)
		return workEntity?.isPending ?: false
	}
}