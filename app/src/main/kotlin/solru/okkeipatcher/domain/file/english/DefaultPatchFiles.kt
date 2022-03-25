package solru.okkeipatcher.domain.file.english

import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPatchFiles @Inject constructor(streamCopier: StreamCopier) {
	val scripts: VerifiableFile by lazy { Scripts(streamCopier) }
}