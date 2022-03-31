package solru.okkeipatcher.domain.gamefile.english

import solru.okkeipatcher.di.factory.ObbDownloadOperationFactory
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.AbstractObb
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloadOperationFactory: ObbDownloadOperationFactory,
	commonFiles: CommonFiles
) : AbstractObb(commonFiles) {

	private val obbDownloadOperation = obbDownloadOperationFactory.create(patchRepository, commonFiles)

	override fun patch() = obbDownloadOperation
	override fun update() = patch()
}