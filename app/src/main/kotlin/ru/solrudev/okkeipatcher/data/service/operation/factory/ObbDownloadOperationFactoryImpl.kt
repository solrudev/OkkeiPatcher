package ru.solrudev.okkeipatcher.data.service.operation.factory

import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.service.operation.ObbDownloadOperation
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactory
import javax.inject.Inject

class ObbDownloadOperationFactoryImpl @Inject constructor(
	private val obbRepository: ObbRepository,
	private val fileDownloader: FileDownloader
) : ObbDownloadOperationFactory {

	override fun create(obbPatchFile: PatchFile) = ObbDownloadOperation(obbPatchFile, obbRepository, fileDownloader)
}