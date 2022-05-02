package ru.solrudev.okkeipatcher.domain.file

import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class TempSaveData(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiStorage.backup.absolutePath, "SAVEDATA_TEMP.DAT"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}