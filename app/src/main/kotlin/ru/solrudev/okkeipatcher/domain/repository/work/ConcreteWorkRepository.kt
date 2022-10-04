package ru.solrudev.okkeipatcher.domain.repository.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.model.Work

interface ConcreteWorkRepository {
	suspend fun enqueueWork(): Work
	fun getPendingWorkFlow(): Flow<Work>
}

interface PatchWorkRepository : ConcreteWorkRepository
interface RestoreWorkRepository : ConcreteWorkRepository
interface DownloadUpdateWorkRepository : ConcreteWorkRepository