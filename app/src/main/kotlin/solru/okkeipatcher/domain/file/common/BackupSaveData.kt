package solru.okkeipatcher.domain.file.common

import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
import java.io.File

class BackupSaveData(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "SAVEDATA.DAT"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}