package ru.solrudev.okkeipatcher.data.repository.app.work

import androidx.lifecycle.asFlow
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.repository.work.ConcreteWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.repository.app.work.mapper.toWork
import ru.solrudev.okkeipatcher.data.worker.ForegroundOperationWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

open class ConcreteWorkRepositoryImpl<T : ForegroundOperationWorker>(
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
		workRepository.add(workRequest.id)
		workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.KEEP, workRequest)
		return Work(workRequest.id, workLabel)
	}

	override fun getPendingWorkFlow() = workManager
		.getWorkInfosForUniqueWorkLiveData(workName)
		.asFlow()
		.mapNotNull { workInfoList -> workInfoList.firstOrNull() }
		.onEach { workInfo ->
			if (workInfo.state == WorkInfo.State.CANCELLED) {
				workRepository.updateIsPending(workInfo.id, isPending = false)
			}
		}
		.distinctUntilChangedBy { it.id }
		.filter { workRepository.getIsPending(it.id) }
		.map { it.toWork(workLabel) }
}