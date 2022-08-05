package ru.solrudev.okkeipatcher.domain.repository.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.model.WorkState
import java.util.*

interface WorkRepository {
	suspend fun add(workId: UUID)
	suspend fun updateIsPending(workId: UUID, isPending: Boolean)
	suspend fun getIsPending(workId: UUID): Boolean
	fun cancelWork(workId: UUID)
	fun getWorkStateFlow(workId: UUID): Flow<WorkState>
}