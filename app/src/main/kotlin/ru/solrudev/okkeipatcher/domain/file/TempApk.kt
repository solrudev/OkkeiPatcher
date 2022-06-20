package ru.solrudev.okkeipatcher.domain.file

import ru.solrudev.okkeipatcher.OkkeiApplication
import ru.solrudev.okkeipatcher.domain.externalDir
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class TempApk(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiApplication.context.externalDir.absolutePath, "base.apk"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.backup_apk_hash.name)
}