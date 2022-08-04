package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.data.repository.work.mapper.toWork
import ru.solrudev.okkeipatcher.data.worker.ForegroundWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.ConcreteWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository

open class ConcreteWorkRepositoryImpl<T : ForegroundWorker>(
	private val workName: String,
	private val workLabel: LocalizedString,
	private val workerClass: Class<T>,
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : ConcreteWorkRepository {

	override suspend fun enqueueWork(): Work {
		val workRequest = OneTimeWorkRequest.Builder(workerClass)
			.addTag(workName)
			.build()
		val work = Work(workRequest.id, workLabel)
		workRepository.add(work)
		workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.KEEP, workRequest)
		return work
	}

	override fun getWork() = workManager
		.getWorkInfosForUniqueWork(workName)
		.get()
		.firstOrNull()
		?.toWork(workLabel)
}