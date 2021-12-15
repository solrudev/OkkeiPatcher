package solru.okkeipatcher.model.files.common

import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class TempSaveData(ioService: IoService) :
	VerifiableFile(
		JavaFile(
			File(OkkeiStorage.backup.absolutePath, "SAVEDATA_TEMP.DAT"),
			ioService
		), ProgressProviderImpl()
	) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}