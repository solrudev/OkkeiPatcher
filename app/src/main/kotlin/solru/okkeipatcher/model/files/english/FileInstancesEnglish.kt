package solru.okkeipatcher.model.files.english

import solru.okkeipatcher.io.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import javax.inject.Inject

class FileInstancesEnglish @Inject constructor(ioService: IoService) {
	val scripts: VerifiableFile by lazy { Scripts(ioService) }
}