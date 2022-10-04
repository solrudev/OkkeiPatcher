package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import javax.inject.Inject

class DownloadUpdateOperationFactory @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : OperationFactory<Result> {

	override suspend fun create() = okkeiPatcherRepository.downloadUpdate()
}