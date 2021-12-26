package solru.okkeipatcher.model.files.common

import android.os.Environment
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.IoService
import java.io.File

class ObbToPatch(ioService: IoService) : VerifiableFile(
	@Suppress("DEPRECATION")
	JavaFile(
		File(
			Environment.getExternalStorageDirectory(),
			"Android/obb/com.mages.chaoschild_jp/main.87.com.mages.chaoschild_jp.obb"
		), ioService
	)
) {
	override suspend fun verify() = exists && compareBySharedPreferences(CommonFileHashKey.patched_obb_hash.name)
}