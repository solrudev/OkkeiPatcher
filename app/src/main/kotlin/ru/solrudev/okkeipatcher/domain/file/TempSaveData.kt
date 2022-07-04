package ru.solrudev.okkeipatcher.domain.file

import ru.solrudev.okkeipatcher.OkkeiApplication
import ru.solrudev.okkeipatcher.domain.backupDir
import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import java.io.File

class TempSaveData(streamCopier: StreamCopier) : VerifiableFile(
	JavaFile(
		File(OkkeiApplication.context.backupDir, "SAVEDATA_TEMP.DAT"), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.save_data_hash.name)
}