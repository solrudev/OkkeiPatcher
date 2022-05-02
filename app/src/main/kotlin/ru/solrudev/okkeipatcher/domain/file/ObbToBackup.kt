package ru.solrudev.okkeipatcher.domain.file

import android.os.Environment
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import java.io.File

class ObbToBackup(streamCopier: StreamCopier) : VerifiableFile(
	@Suppress("DEPRECATION")
	JavaFile(
		File(
			Environment.getExternalStorageDirectory(),
			"Android/obb/com.mages.chaoschild_jp/main.87.com.mages.chaoschild_jp.obb"
		), streamCopier
	)
) {
	override fun verify() = compareBySharedPreferences(CommonFileHashKey.backup_obb_hash.name)
}