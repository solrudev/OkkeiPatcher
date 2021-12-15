package solru.okkeipatcher.model.files.common

import android.os.Environment
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

class ObbToPatch(ioService: IoService) : VerifiableFile(
	@Suppress("DEPRECATION")
	JavaFile(
		File(
			Environment.getExternalStorageDirectory(),
			"Android/obb/com.mages.chaoschild_jp/main.87.com.mages.chaoschild_jp.obb"
		), ioService
	), ProgressProviderImpl()
) {
	override suspend fun verify() =
		exists && compareBySharedPreferences(CommonFileHashKey.patched_obb_hash.name)
}