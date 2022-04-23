package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.asWork
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.domain.worker.MockWorker
import javax.inject.Inject

private const val PATCH_WORK_NAME = "PatchWork"

class MockPatchWorkRepositoryImpl @Inject constructor(
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : PatchWorkRepository {

	override suspend fun enqueuePatchWork(): Work {
		val workRequest = OneTimeWorkRequest.Builder(MockWorker::class.java)
			.addTag(PATCH_WORK_NAME)
			.build()
		workManager.enqueueUniqueWork(PATCH_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest)
		val work = Work(workRequest.id)
		workRepository.add(work)
		return work
	}

	override fun getPatchWork() = workManager
		.getWorkInfosForUniqueWork(PATCH_WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork()
}