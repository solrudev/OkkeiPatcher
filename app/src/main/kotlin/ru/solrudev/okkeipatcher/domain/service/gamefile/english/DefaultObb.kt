package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import ru.solrudev.okkeipatcher.domain.file.CommonFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.Obb
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactory
import java.io.File
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloadOperationFactory: ObbDownloadOperationFactory,
	commonFiles: CommonFiles
) : Obb(commonFiles) {

	private val obbDownloadOperation =
		obbDownloadOperationFactory.create(File(commonFiles.obbToPatch.fullPath), patchRepository)

	override fun patch() = obbDownloadOperation
	override fun update() = patch()
}