package ru.solrudev.okkeipatcher.domain.file.common

import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class BackupObb(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "main.87.com.mages.chaoschild_jp.obb"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.backup_obb_hash.name)
}