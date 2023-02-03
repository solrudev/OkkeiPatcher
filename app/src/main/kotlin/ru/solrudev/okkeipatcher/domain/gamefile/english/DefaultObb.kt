package ru.solrudev.okkeipatcher.domain.gamefile.english

import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.gamefile.Obb
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbDownloadOperationFactory
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloadOperationFactory: ObbDownloadOperationFactory,
	obbRepository: ObbRepository,
	obbBackupRepository: ObbBackupRepository
) : Obb(obbRepository, obbBackupRepository) {

	private val obbDownloadOperation = obbDownloadOperationFactory.create(patchRepository.obb)

	override fun patch() = obbDownloadOperation
	override fun update() = patch()
}