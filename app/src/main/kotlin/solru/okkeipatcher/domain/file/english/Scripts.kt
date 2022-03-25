package solru.okkeipatcher.domain.file.english

import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
import java.io.File

class Scripts(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.external.absolutePath, "scripts.zip"), streamCopier
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(PatchFileHashKey.scripts_hash.name)
}