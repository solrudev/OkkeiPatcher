package solru.okkeipatcher.model.files.common

import android.os.Environment
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class ObbToPatch(ioService: IoService) : VerifiableFileWrapper(
	@Suppress("DEPRECATION")
	JavaFile(
		File(
			Environment.getExternalStorageDirectory(),
			"Android/obb/com.mages.chaoschild_jp/main.87.com.mages.chaoschild_jp.obb"
		), ioService
	), ioService
) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.patched_obb_hash.name)
}