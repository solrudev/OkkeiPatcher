package solru.okkeipatcher.domain.usecase.work.impl

import solru.okkeipatcher.data.database.model.WorkModel
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.WorkRepository
import solru.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import javax.inject.Inject

class CompleteWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	CompleteWorkUseCase {

	override suspend fun invoke(work: Work) {
		workRepository.getByWorkId(work.id)?.let { workEntity ->
			if (!workEntity.isPending) {
				return
			}
			workRepository.updateIsPendingByWorkId(workEntity, isPending = false)
			return
		}
		workRepository.add(WorkModel(workId = work.id, isPending = false))
	}
}