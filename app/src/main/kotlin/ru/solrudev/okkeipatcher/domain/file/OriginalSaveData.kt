package ru.solrudev.okkeipatcher.domain.file

import android.os.Environment
import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import ru.solrudev.okkeipatcher.io.file.DocumentFile
import ru.solrudev.okkeipatcher.io.file.File
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.file.VerifiableFile

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