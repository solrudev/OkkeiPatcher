package ru.solrudev.okkeipatcher.domain.file.english

import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class Scripts(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.external.absolutePath, "scripts.zip"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(PatchFileHashKey.scripts_hash.name)
}