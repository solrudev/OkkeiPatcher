package solru.okkeipatcher.domain.usecase.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.Work
import solru.okkeipatcher.data.db.entity.WorkEntity
import solru.okkeipatcher.domain.usecase.EnqueueRestoreWorkUseCase
import solru.okkeipatcher.repository.WorkRepository
import solru.okkeipatcher.workers.RestoreWorker
import javax.inject.Inject

class EnqueueRestoreWorkUseCaseImpl @Inject constructor(private val workRepository: WorkRepository) :
	EnqueueRestoreWorkUseCase {

	override suspend fun invoke(): Work {
		val workRequest = OneTimeWorkRequest.from(RestoreWorker::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			RestoreWorker.WORK_NAME,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		workRepository.add(WorkEntity(workId = workRequest.id))
		return Work(workRequest.id)
	}
}