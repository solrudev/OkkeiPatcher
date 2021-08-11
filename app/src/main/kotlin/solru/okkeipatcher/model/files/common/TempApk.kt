package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class TempApk(ioService: IoService) :
	VerifiableFileWrapper(
		JavaFile(File(OkkeiStorage.external.absolutePath, "base.apk"), ioService),
		ioService
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.backup_apk_hash.name)
}