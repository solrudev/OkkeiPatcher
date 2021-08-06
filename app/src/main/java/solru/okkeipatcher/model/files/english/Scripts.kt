package solru.okkeipatcher.model.files.english

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class Scripts(ioService: IoService) :
	VerifiableFileWrapper(
		JavaFile(
			File(OkkeiStorage.external.absolutePath, "scripts.zip"),
			ioService
		), ioService
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(FileHashKey.scripts_hash.name)
}