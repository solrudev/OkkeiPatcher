package solru.okkeipatcher.domain.file.common

import android.os.Environment
import solru.okkeipatcher.io.file.DocumentFile
import solru.okkeipatcher.io.file.File
import solru.okkeipatcher.io.file.JavaFile
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.StreamCopier

sealed class OriginalSaveData(
	implementation: File,
	private val streamCopier: StreamCopier
) : VerifiableFile(implementation) {

	override fun verify() = compareByFile(BackupSaveData(streamCopier))

	class JavaFileImpl(streamCopier: StreamCopier) :
		OriginalSaveData(JavaFile(java.io.File(filePath, fileName), streamCopier), streamCopier)

	class DocumentFileImpl(streamCopier: StreamCopier) :
		OriginalSaveData(DocumentFile(filePath, fileName, streamCopier), streamCopier)

	@Suppress("DEPRECATION")
	companion object {
		const val fileName = "SAVEDATA.DAT"
		val filePath =
			"${Environment.getExternalStorageDirectory().absolutePath}/Android/data/com.mages.chaoschild_jp/files"
	}
}