package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.work.mapper.asWork
import ru.solrudev.okkeipatcher.data.worker.RestoreWorker
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

private const val RESTORE_WORK_NAME = "RestoreWork"
private val workLabel = LocalizedString.resource(R.string.restoring)

class RestoreWorkRepositoryImpl @Inject constructor(
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : RestoreWorkRepository {

	override suspend fun enqueueRestoreWork(): Work {
		val workRequest = OneTimeWorkRequest.Builder(RestoreWorker::class.java)
			.addTag(RESTORE_WORK_NAME)
			.build()
		workManager.enqueueUniqueWork(RESTORE_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest)
		val work = Work(workRequest.id, workLabel)
		workRepository.add(work)
		return work
	}

	override fun getRestoreWork() = workManager
		.getWorkInfosForUniqueWork(RESTORE_WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork(workLabel)
}