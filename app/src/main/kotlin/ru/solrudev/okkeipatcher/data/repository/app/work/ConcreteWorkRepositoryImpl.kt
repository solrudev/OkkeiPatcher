package ru.solrudev.okkeipatcher.data.repository.app.work

import androidx.lifecycle.asFlow
import androidx.work.*
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.repository.work.ConcreteWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.repository.app.work.mapper.toWork
import ru.solrudev.okkeipatcher.data.worker.ForegroundOperationWorker
import ru.solrudev.okkeipatcher.data.worker.util.extension.putSerializable
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

const val WORK_LABEL_KEY = "WORK_LABEL"

open class ConcreteWorkRepositoryImpl<T : ForegroundOperationWorker>(
	private val workName: String,
	private val workLabel: LocalizedString,
	private val workerClass: Class<T>,
	private val workRepository: WorkRepository,
	private val workManager: WorkManager
) : ConcreteWorkRepository {

	override suspend fun enqueueWork(): Work {
		workManager.getWorkInfosByTag(workName).await()
			.firstOrNull { workInfo -> !workInfo.state.isFinished }
			?.let { workInfo -> return Work(workInfo.id, workLabel) }
		val workRequest = OneTimeWorkRequest.Builder(workerClass)
			.setInputData(Data.Builder().putSerializable(WORK_LABEL_KEY, workLabel).build())
			.addTag(workName)
			.build()
		workRepository.add(workRequest.id)
		workManager.enqueue(workRequest).await()
		return Work(workRequest.id, workLabel)
	}

	override fun getPendingWorkFlow() = workManager
		.getWorkInfosByTagLiveData(workName)
		.asFlow()
		.mapNotNull { workInfoList ->
			workInfoList.firstOrNull { workRepository.getIsPending(it.id) }
		}
		.onEach { workInfo ->
			if (workInfo.state == WorkInfo.State.CANCELLED) {
				workRepository.updateIsPending(workInfo.id, isPending = false)
			}
		}
		.distinctUntilChangedBy { it.id }
		.map { it.toWork(workLabel) }
}