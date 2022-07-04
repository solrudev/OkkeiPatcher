package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.patch.ObbDataRepository
import ru.solrudev.okkeipatcher.domain.service.HttpDownloader
import ru.solrudev.okkeipatcher.domain.service.operation.ObbDownloadOperation
import java.io.File
import javax.inject.Inject

interface ObbDownloadOperationFactory {
	fun create(obb: File, obbDataRepository: ObbDataRepository): Operation<Unit>
}

class ObbDownloadOperationFactoryImpl @Inject constructor(
	private val httpDownloader: HttpDownloader
) : ObbDownloadOperationFactory {

	override fun create(obb: File, obbDataRepository: ObbDataRepository) =
		ObbDownloadOperation(obb, obbDataRepository, httpDownloader)
}