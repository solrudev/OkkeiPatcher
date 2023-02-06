package ru.solrudev.okkeipatcher.app.operation.factory

import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.operation.factory.OperationFactory
import javax.inject.Inject

class DownloadUpdateOperationFactory @Inject constructor(
	private val okkeiPatcherRepository: OkkeiPatcherRepository
) : OperationFactory<Result> {

	override suspend fun create() = okkeiPatcherRepository.downloadUpdate()
}