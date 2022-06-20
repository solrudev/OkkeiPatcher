package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.Obb
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ObbDownloadOperationFactory
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloadOperationFactory: ObbDownloadOperationFactory,
	obbRepository: ObbRepository
) : Obb(obbRepository) {

	private val obbDownloadOperation = obbDownloadOperationFactory.create(obbRepository.obbFile, patchRepository)

	override fun patch() = obbDownloadOperation
	override fun update() = patch()
}