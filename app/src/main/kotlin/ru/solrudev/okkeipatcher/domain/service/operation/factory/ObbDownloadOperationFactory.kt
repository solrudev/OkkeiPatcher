package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.service.HttpDownloader
import ru.solrudev.okkeipatcher.domain.service.operation.ObbDownloadOperation
import javax.inject.Inject

interface ObbDownloadOperationFactory {
	fun create(obbPatchFile: PatchFile): Operation<Unit>
}

class ObbDownloadOperationFactoryImpl @Inject constructor(
	private val obbRepository: ObbRepository,
	private val httpDownloader: HttpDownloader
) : ObbDownloadOperationFactory {

	override fun create(obbPatchFile: PatchFile) = ObbDownloadOperation(obbPatchFile, obbRepository, httpDownloader)
}