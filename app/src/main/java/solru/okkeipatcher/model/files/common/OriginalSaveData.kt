package solru.okkeipatcher.model.files.common

import android.os.Environment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import solru.okkeipatcher.io.JavaFile
import solru.okkeipatcher.io.SafFile
import solru.okkeipatcher.io.VerifiableFileWrapper
import solru.okkeipatcher.io.base.FileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

open class OriginalSaveData constructor(implementation: FileWrapper, ioService: IoService) :
	VerifiableFileWrapper(implementation, ioService) {

	@ExperimentalCoroutinesApi
	override suspend fun verify() = exists && compareByFile(BackupSaveData(ioService))

	@Suppress("DEPRECATION")
	companion object {
		const val fileName = "SAVEDATA.DAT"
		val fullPath =
			"${Environment.getExternalStorageDirectory().absolutePath}/Android/data/com.mages.chaoschild_jp/files/$fileName"
	}

	class JavaFileImpl(ioService: IoService) :
		OriginalSaveData(JavaFile(File(fullPath, fileName), ioService), ioService)

	class SafFileImpl(ioService: IoService) :
		OriginalSaveData(SafFile(fullPath, fileName, ioService), ioService)
}