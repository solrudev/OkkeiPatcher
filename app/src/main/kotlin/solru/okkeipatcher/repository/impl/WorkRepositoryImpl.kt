package solru.okkeipatcher.repository.impl

import solru.okkeipatcher.data.db.dao.WorkDao
import solru.okkeipatcher.data.db.entity.WorkEntity
import solru.okkeipatcher.repository.WorkRepository
import java.util.*
import javax.inject.Inject

class WorkRepositoryImpl @Inject constructor(private val workDao: WorkDao) : WorkRepository {

	override suspend fun add(work: WorkEntity) {
		workDao.insert(work)
	}

	override suspend fun update(work: WorkEntity) {
		workDao.update(work)
	}

	override suspend fun getByWorkId(id: UUID) = workDao.getByWorkId(id)
}