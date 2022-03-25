package solru.okkeipatcher.data.repository

import solru.okkeipatcher.data.database.dao.WorkDao
import solru.okkeipatcher.data.database.model.WorkModel
import solru.okkeipatcher.domain.repository.WorkRepository
import java.util.*
import javax.inject.Inject

class WorkRepositoryImpl @Inject constructor(private val workDao: WorkDao) : WorkRepository {

	override suspend fun add(work: WorkModel) {
		workDao.insert(work)
	}

	override suspend fun updateIsPendingByWorkId(work: WorkModel, isPending: Boolean) {
		workDao.updateIsPendingByWorkId(work.id, isPending)
	}

	override suspend fun getByWorkId(id: UUID) = workDao.getByWorkId(id)
}