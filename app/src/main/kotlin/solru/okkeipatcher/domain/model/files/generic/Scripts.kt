package solru.okkeipatcher.domain.model.files.generic

import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.StreamCopier
import java.io.File

class Scripts(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.external.absolutePath, "scripts.zip"), streamCopier
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(PatchFileHashKey.scripts_hash.name)
}