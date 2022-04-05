package solru.okkeipatcher.data.repository.work

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.model.asWork
import solru.okkeipatcher.domain.repository.work.RestoreWorkRepository
import solru.okkeipatcher.domain.repository.work.WorkRepository
import solru.okkeipatcher.domain.worker.RestoreWorker
import javax.inject.Inject

private const val RESTORE_WORK_NAME = "RestoreWork"

class RestoreWorkRepositoryImpl @Inject constructor(
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : RestoreWorkRepository {

	override suspend fun enqueueRestoreWork(): Work {
		val workRequest = OneTimeWorkRequest.from(RestoreWorker::class.java)
		workManager.enqueueUniqueWork(RESTORE_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest)
		val work = Work(workRequest.id)
		workRepository.add(work)
		return work
	}

	override fun getRestoreWork() = workManager
		.getWorkInfosForUniqueWork(RESTORE_WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork()
}