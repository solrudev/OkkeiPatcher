package solru.okkeipatcher.core.model.files.english

import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.StreamCopier
import javax.inject.Inject

class DefaultFiles @Inject constructor(streamCopier: StreamCopier) {
	val scripts: VerifiableFile by lazy { Scripts(streamCopier) }
}