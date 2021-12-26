package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.IoService
import java.io.File

class TempSaveData(ioService: IoService) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "SAVEDATA_TEMP.DAT"), ioService
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}