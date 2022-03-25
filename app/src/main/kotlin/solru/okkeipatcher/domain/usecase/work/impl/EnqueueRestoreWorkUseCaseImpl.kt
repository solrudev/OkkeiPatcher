package solru.okkeipatcher.domain.usecase.work.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.WorkRepository
import solru.okkeipatcher.domain.usecase.work.EnqueueRestoreWorkUseCase
import solru.okkeipatcher.domain.worker.RestoreWorker
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
		val work = Work(workRequest.id)
		workRepository.add(work)
		return work
	}
}