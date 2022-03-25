package solru.okkeipatcher.domain.file.common

import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
import java.io.File

class BackupApk(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "backup.apk"), streamCopier
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.backup_apk_hash.name)
}