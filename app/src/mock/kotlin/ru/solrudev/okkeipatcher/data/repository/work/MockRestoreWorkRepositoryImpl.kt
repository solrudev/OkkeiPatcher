package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.work.mapper.toWork
import ru.solrudev.okkeipatcher.data.worker.MockWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

private const val RESTORE_WORK_NAME = "RestoreWork"
private val workLabel = LocalizedString.resource(R.string.work_label_restoring)

class MockRestoreWorkRepositoryImpl @Inject constructor(
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : RestoreWorkRepository {

	override suspend fun enqueueRestoreWork(): Work {
		val workRequest = OneTimeWorkRequest.Builder(MockWorker::class.java)
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
		?.toWork(workLabel)
}