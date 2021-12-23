package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class BackupObb(ioService: IoService) : VerifiableFile(
	JavaFile(
		File(
			OkkeiStorage.backup.absolutePath,
			"main.87.com.mages.chaoschild_jp.obb"
		), ioService
	), ProgressProviderImpl()
) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.backup_obb_hash.name)
}