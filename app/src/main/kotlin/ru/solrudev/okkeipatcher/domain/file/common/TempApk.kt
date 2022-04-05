package ru.solrudev.okkeipatcher.domain.file.common

import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class TempApk(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.external.absolutePath, "base.apk"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.backup_apk_hash.name)
}