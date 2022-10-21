package ru.solrudev.okkeipatcher.domain.repository.app

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.domain.model.Work

interface OkkeiPatcherRepository {
	val isUpdateAvailable: Flow<Boolean>
	suspend fun getUpdateData(refresh: Boolean): OkkeiPatcherUpdateData
	suspend fun enqueueUpdateDownloadWork(): Work
	suspend fun installUpdate(): Result
	fun downloadUpdate(): Operation<Result>
	fun getPendingUpdateDownloadWorkFlow(): Flow<Work>
}