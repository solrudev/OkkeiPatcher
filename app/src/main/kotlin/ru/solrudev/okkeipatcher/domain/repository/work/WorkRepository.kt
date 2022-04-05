package ru.solrudev.okkeipatcher.domain.repository.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.model.WorkState

interface WorkRepository {
	suspend fun add(work: Work)
	suspend fun updateIsPending(work: Work, isPending: Boolean)
	suspend fun getIsPending(work: Work): Boolean
	fun cancelWork(work: Work)
	fun getWorkStateFlow(work: Work): Flow<WorkState>
}