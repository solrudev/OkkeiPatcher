package solru.okkeipatcher.model.files.english

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class Scripts(ioService: IoService) :
	VerifiableFile(
		JavaFile(
			File(OkkeiStorage.external.absolutePath, "scripts.zip"),
			ioService
		), ProgressProviderImpl()
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(FileHashKey.scripts_hash.name)
}