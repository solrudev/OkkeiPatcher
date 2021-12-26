package solru.okkeipatcher.model.files.english

import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.IoService
import javax.inject.Inject

class FilesEnglish @Inject constructor(ioService: IoService) {
	val scripts: VerifiableFile by lazy { Scripts(ioService) }
}