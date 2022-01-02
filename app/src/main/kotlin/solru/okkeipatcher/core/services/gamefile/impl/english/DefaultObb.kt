package solru.okkeipatcher.core.services.gamefile.impl.english

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.services.gamefile.impl.Obb
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.di.factory.ObbDownloaderFactory
import solru.okkeipatcher.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class DefaultObb @Inject constructor(
	patchRepository: DefaultPatchRepository,
	obbDownloaderFactory: ObbDownloaderFactory,
	commonFiles: CommonFiles
) : Obb(commonFiles) {

	private val obbDownloader = obbDownloaderFactory.create(patchRepository, commonFiles)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val status = merge(super.status, obbDownloader.status)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, obbDownloader.progress)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val messages = merge(super.messages, obbDownloader.messages)

	override suspend fun patch() {
		progressPublisher.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_obb))
		if (commonFiles.obbToPatch.verify()) return
		obbDownloader.download()
	}

	override suspend fun update() {
		progressPublisher.mutableProgress.reset()
		commonFiles.obbToPatch.delete()
		obbDownloader.download()
	}
}