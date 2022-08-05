package ru.solrudev.okkeipatcher.data.repository.work

import androidx.lifecycle.asFlow
import androidx.work.WorkManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import ru.solrudev.okkeipatcher.data.database.dao.WorkDao
import ru.solrudev.okkeipatcher.data.database.model.WorkModel
import ru.solrudev.okkeipatcher.data.repository.work.mapper.WorkStateMapper
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import java.util.*
import javax.inject.Inject

class WorkRepositoryImpl @Inject constructor(
	private val workDao: WorkDao,
	private val workManager: WorkManager,
	private val workStateMapper: WorkStateMapper
) : WorkRepository {

	override suspend fun add(workId: UUID) {
		val workModel = WorkModel(workId = workId)
		workDao.insert(workModel)
	}

	override suspend fun updateIsPending(workId: UUID, isPending: Boolean) =
		workDao.updateIsPendingByWorkId(workId, isPending)

	override suspend fun getIsPending(workId: UUID): Boolean {
		val isPending = workDao.getIsPendingByWorkId(workId)
		return isPending ?: false
	}

	override fun cancelWork(workId: UUID) {
		workManager.cancelWorkById(workId)
	}

	override fun getWorkStateFlow(workId: UUID) = flow {
		supervisorScope {
			workManager
				.getWorkInfoByIdLiveData(workId)
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