package solru.okkeipatcher.core.model.files.generic

import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.StreamCopier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPatchFiles @Inject constructor(streamCopier: StreamCopier) {
	val scripts: VerifiableFile by lazy { Scripts(streamCopier) }
}