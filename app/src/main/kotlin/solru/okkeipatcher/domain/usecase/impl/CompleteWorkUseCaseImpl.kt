package solru.okkeipatcher.domain.usecase.impl

import solru.okkeipatcher.data.Work
import solru.okkeipatcher.data.db.entity.WorkEntity
import solru.okkeipatcher.domain.usecase.CompleteWorkUseCase
import solru.okkeipatcher.repository.WorkRepository
import javax.inject.Inject

class CompleteWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	CompleteWorkUseCase {

	override suspend fun invoke(work: Work) {
		workRepository.getByWorkId(work.id)?.let { workEntity ->
			if (!workEntity.isPending) {
				return
			}
			workRepository.updateIsPending(workEntity, isPending = false)
			return
		}
		workRepository.add(WorkEntity(workId = work.id, isPending = false))
	}
}