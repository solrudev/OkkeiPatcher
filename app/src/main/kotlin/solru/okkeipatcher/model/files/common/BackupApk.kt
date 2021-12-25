package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class BackupApk(ioService: IoService) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "backup.apk"), ioService
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.backup_apk_hash.name)
}