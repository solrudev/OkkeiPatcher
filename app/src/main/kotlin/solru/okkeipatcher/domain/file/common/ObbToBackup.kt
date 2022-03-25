package solru.okkeipatcher.domain.file.common

import android.os.Environment
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier
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
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.backup_obb_hash.name)
}