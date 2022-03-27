package solru.okkeipatcher.domain.gamefile.impl.english

import solru.okkeipatcher.di.factory.ObbDownloadOperationFactory
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.impl.AbstractObb
import solru.okkeipatcher.domain.operation.AbstractOperation
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloadOperationFactory: ObbDownloadOperationFactory,
	commonFiles: CommonFiles
) : AbstractObb(commonFiles) {

	private val obbDownloadOperation = obbDownloadOperationFactory.create(patchRepository, commonFiles)

	override fun patch() = object : AbstractOperation<Unit>() {

		override val status = obbDownloadOperation.status
		override val progressDelta = obbDownloadOperation.progressDelta
		override val progressMax = obbDownloadOperation.progressMax

		override suspend fun invoke() = obbDownloadOperation()
	}

	override fun update() = patch()
}