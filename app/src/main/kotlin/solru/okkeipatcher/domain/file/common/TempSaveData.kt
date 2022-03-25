package solru.okkeipatcher.domain.file.common

import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
import java.io.File

class TempSaveData(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "SAVEDATA_TEMP.DAT"), streamCopier
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}