package solru.okkeipatcher.domain.usecase.work.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.database.model.WorkModel
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.WorkRepository
import solru.okkeipatcher.domain.usecase.work.EnqueuePatchWorkUseCase
import solru.okkeipatcher.domain.worker.PatchWorker
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
		workRepository.add(WorkModel(workId = workRequest.id))
		return Work(workRequest.id)
	}
}