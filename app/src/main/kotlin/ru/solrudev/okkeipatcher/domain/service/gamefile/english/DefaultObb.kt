package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import ru.solrudev.okkeipatcher.di.factory.ObbDownloadOperationFactory
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.Obb
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloadOperationFactory: ObbDownloadOperationFactory,
	commonFiles: CommonFiles
) : Obb(commonFiles) {

	private val obbDownloadOperation = obbDownloadOperationFactory.create(patchRepository, commonFiles)

	override fun patch() = obbDownloadOperation
	override fun update() = patch()
}