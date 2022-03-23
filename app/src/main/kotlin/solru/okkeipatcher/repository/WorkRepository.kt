package solru.okkeipatcher.repository

import solru.okkeipatcher.data.db.entity.WorkEntity
import java.util.*

interface WorkRepository {
	suspend fun add(work: WorkEntity)
	suspend fun update(work: WorkEntity)
	suspend fun getByWorkId(id: UUID): WorkEntity?
}