package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.service.HttpDownloader
import ru.solrudev.okkeipatcher.domain.service.operation.ObbDownloadOperation
import java.io.File
import javax.inject.Inject

interface ObbDownloadOperationFactory {
	fun create(obb: File, obbPatchFile: PatchFile): Operation<Unit>
}

class ObbDownloadOperationFactoryImpl @Inject constructor(
	private val httpDownloader: HttpDownloader
) : ObbDownloadOperationFactory {

	override fun create(obb: File, obbPatchFile: PatchFile) = ObbDownloadOperation(obb, obbPatchFile, httpDownloader)
}