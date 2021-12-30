package solru.okkeipatcher.core.services.gamefile.impl.english

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.model.files.generic.DefaultPatchFiles
import solru.okkeipatcher.core.services.gamefile.impl.BaseApk
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.di.factory.ScriptsPatcherFactory
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class DefaultApk @Inject constructor(
	patchRepository: DefaultPatchRepository,
	scriptsPatcherFactory: ScriptsPatcherFactory,
	patchFiles: DefaultPatchFiles,
	commonFiles: CommonFiles,
	streamCopier: StreamCopier,
	ioDispatcher: CoroutineDispatcher
) : BaseApk(commonFiles, streamCopier, ioDispatcher) {

	private val scriptsPatcher = scriptsPatcherFactory.create(this, patchRepository, patchFiles.scripts)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val status = merge(super.status, scriptsPatcher.status)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, scriptsPatcher.progress)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val messages = merge(super.messages, scriptsPatcher.messages)

	override suspend fun patch() {
		progressPublisher.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (verifyBackupIntegrity() && commonFiles.signedApk.verify()) {
			installPatched()
			return
		}
		scriptsPatcher.patch()
		installPatched()
	}
}