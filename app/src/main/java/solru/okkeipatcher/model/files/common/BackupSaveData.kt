package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class BackupSaveData(ioService: IoService) :
	VerifiableFileWrapper(
		JavaFile(
			File(OkkeiStorage.backup.absolutePath, "SAVEDATA.DAT"),
			ioService
		), ioService
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}