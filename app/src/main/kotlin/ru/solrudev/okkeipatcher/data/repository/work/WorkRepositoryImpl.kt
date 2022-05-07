package ru.solrudev.okkeipatcher.data.repository.work

import androidx.lifecycle.asFlow
import androidx.work.WorkManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import ru.solrudev.okkeipatcher.data.database.dao.WorkDao
import ru.solrudev.okkeipatcher.data.database.model.WorkModel
import ru.solrudev.okkeipatcher.data.repository.work.mapper.WorkStateMapper
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

class WorkRepositoryImpl @Inject constructor(
	private val workDao: WorkDao,
	private val workManager: WorkManager,
	private val workStateMapper: WorkStateMapper
) : WorkRepository {

	override suspend fun add(work: Work) {
		val workModel = WorkModel(workId = work.id)
		workDao.insert(workModel)
	}

	override suspend fun updateIsPending(work: Work, isPending: Boolean) =
		workDao.updateIsPendingByWorkId(work.id, isPending)

	override suspend fun getIsPending(work: Work): Boolean {
		val isPending = workDao.getIsPendingByWorkId(work.id)
		return isPending ?: false
	}

	override fun cancelWork(work: Work) {
		workManager.cancelWorkById(work.id)
	}

	override fun getWorkStateFlow(work: Work) = flow {
		supervisorScope {
			workManager
				.getWorkInfoByIdLiveData(work.id)
				.asFlow()
				.collect { workInfo ->
					val workState = workStateMapper(workInfo)
					emit(workState)
					if (workState.isFinished) {
						this@supervisorScope.cancel()
					}
				}
		}
	}
}