package ru.solrudev.okkeipatcher.app.repository.work

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.app.model.Work

interface UniqueWorkRepository {
	suspend fun enqueueWork(): Work
	fun getPendingWorkFlow(): Flow<Work>
}

interface PatchWorkRepository : UniqueWorkRepository
interface RestoreWorkRepository : UniqueWorkRepository
interface DownloadUpdateWorkRepository : UniqueWorkRepository