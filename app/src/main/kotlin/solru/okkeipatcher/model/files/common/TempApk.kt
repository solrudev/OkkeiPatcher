package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.IoService
import java.io.File

class TempApk(ioService: IoService) : VerifiableFile(
	JavaFile(File(OkkeiStorage.external.absolutePath, "base.apk"), ioService),
) {
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.backup_apk_hash.name)
}