package solru.okkeipatcher.domain.gamefile.impl.english

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.di.factory.ScriptsPatcherFactory
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.file.english.DefaultPatchFiles
import solru.okkeipatcher.domain.gamefile.impl.AbstractApk
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.domain.util.extension.reset
import solru.okkeipatcher.io.service.StreamCopier
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
		progressPublisher._progress.reset()
		_status.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (verifyBackupIntegrity() && commonFiles.signedApk.verify()) {
			installPatched(updating = false)
			return
		}
		scriptsPatcher.patch()
		installPatched(updating = false)
	}

	override suspend fun update() {
		progressPublisher._progress.reset()
		commonFiles.tempApk.delete()
		commonFiles.signedApk.delete()
		scriptsPatcher.patch()
		installPatched(updating = true)
	}
}