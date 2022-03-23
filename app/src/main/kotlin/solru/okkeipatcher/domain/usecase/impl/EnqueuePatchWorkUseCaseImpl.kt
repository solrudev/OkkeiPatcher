package solru.okkeipatcher.domain.usecase.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.Work
import solru.okkeipatcher.data.db.entity.WorkEntity
import solru.okkeipatcher.domain.usecase.EnqueuePatchWorkUseCase
import solru.okkeipatcher.repository.WorkRepository
import solru.okkeipatcher.workers.PatchWorker
import javax.inject.Inject

class EnqueuePatchWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	EnqueuePatchWorkUseCase {

	override suspend fun invoke(): Work {
		val workRequest = OneTimeWorkRequest.from(PatchWorker::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			PatchWorker.WORK_NAME,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		workRepository.add(WorkEntity(workId = workRequest.id))
		return Work(workRequest.id)
	}
}