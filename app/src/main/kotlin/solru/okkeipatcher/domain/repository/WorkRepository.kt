package solru.okkeipatcher.domain.repository

import solru.okkeipatcher.data.database.model.WorkModel
import java.util.*

interface WorkRepository {
	suspend fun add(work: WorkModel)
	suspend fun updateIsPendingByWorkId(work: WorkModel, isPending: Boolean)
	suspend fun getByWorkId(id: UUID): WorkModel?
}