package solru.okkeipatcher.domain.services.gamefile.impl.english

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.di.factory.ScriptsPatcherFactory
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.domain.model.files.common.CommonFiles
import solru.okkeipatcher.domain.model.files.generic.DefaultPatchFiles
import solru.okkeipatcher.domain.services.gamefile.impl.AbstractApk
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
	@IoDispatcher ioDispatcher: CoroutineDispatcher
) : AbstractApk(commonFiles, streamCopier, ioDispatcher) {

	private val scriptsPatcher = scriptsPatcherFactory.create(this, patchRepository, patchFiles.scripts)
	override val status = merge(super.status, scriptsPatcher.status)
	override val progress = merge(super.progress, scriptsPatcher.progress)
	override val messages = merge(super.messages, scriptsPatcher.messages)

	override suspend fun patch() {
		progressPublisher.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (verifyBackupIntegrity() && commonFiles.signedApk.verify()) {
			installPatched(updating = false)
			return
		}
		scriptsPatcher.patch()
		installPatched(updating = false)
	}

	override suspend fun update() {
		progressPublisher.mutableProgress.reset()
		commonFiles.tempApk.delete()
		commonFiles.signedApk.delete()
		scriptsPatcher.patch()
		installPatched(updating = true)
	}
}