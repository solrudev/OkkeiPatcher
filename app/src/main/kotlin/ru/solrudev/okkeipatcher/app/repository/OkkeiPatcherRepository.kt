package ru.solrudev.okkeipatcher.app.repository

import kotlinx.coroutines.flow.Flow
import ru.solrudev.okkeipatcher.app.model.OkkeiPatcherUpdateData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation

interface OkkeiPatcherRepository {
	val isUpdateAvailable: Flow<Boolean>
	val isUpdateInstallPending: Flow<Boolean>
	suspend fun getUpdateData(refresh: Boolean): OkkeiPatcherUpdateData
	suspend fun enqueueUpdateDownloadWork(): Work
	suspend fun installUpdate(): Result
	fun downloadUpdate(): Operation<Result>
	fun getPendingUpdateDownloadWorkFlow(): Flow<Work>
}