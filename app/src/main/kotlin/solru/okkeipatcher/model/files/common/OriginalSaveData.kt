package solru.okkeipatcher.model.files.common

import android.os.Environment
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.file.BaseFile
import solru.okkeipatcher.io.file.DocumentFile
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.File

open class OriginalSaveData(implementation: BaseFile, private val ioService: IoService) :
	VerifiableFile(implementation, ProgressProviderImpl()) {

	override suspend fun verify() = exists && compareByFile(BackupSaveData(ioService))

	@Suppress("DEPRECATION")
	companion object {
		const val fileName = "SAVEDATA.DAT"
		val filePath =
			"${Environment.getExternalStorageDirectory().absolutePath}/Android/data/com.mages.chaoschild_jp/files"
	}

	class JavaFileImpl(ioService: IoService) :
		OriginalSaveData(JavaFile(File(filePath, fileName), ioService), ioService)

	class DocumentFileImpl(ioService: IoService) :
		OriginalSaveData(DocumentFile(filePath, fileName, ioService), ioService)
}