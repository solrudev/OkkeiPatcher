package solru.okkeipatcher.model.files.english

import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.StreamCopier
import javax.inject.Inject

class FilesEnglish @Inject constructor(streamCopier: StreamCopier) {
	val scripts: VerifiableFile by lazy { Scripts(streamCopier) }
}