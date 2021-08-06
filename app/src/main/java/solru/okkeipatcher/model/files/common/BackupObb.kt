package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class BackupObb(ioService: IoService) : VerifiableFileWrapper(
	JavaFile(
		File(
			OkkeiStorage.backup.absolutePath,
			"main.87.com.mages.chaoschild_jp.obb"
		), ioService
	), ioService
) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.backup_obb_hash.name)
}