package solru.okkeipatcher.data.repository

import solru.okkeipatcher.data.database.dao.WorkDao
import solru.okkeipatcher.data.database.model.WorkModel
import solru.okkeipatcher.domain.model.Work
import solru.okkeipatcher.domain.repository.WorkRepository
import javax.inject.Inject

class WorkRepositoryImpl @Inject constructor(private val workDao: WorkDao) : WorkRepository {

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
}