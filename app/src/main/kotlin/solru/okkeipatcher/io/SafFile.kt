package solru.okkeipatcher.io

import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.makeFile
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.io.base.FileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.IOException

open class SafFile(fullPath: String, fileName: String, ioService: IoService) :
	FileWrapper(fullPath, fileName, ioService) {

	private val documentFile: DocumentFile? by lazy {
		DocumentFileCompat.fromFullPath(
			MainApplication.context,
			fullPath
		)
	}

	override val exists: Boolean
		get() = documentFile!!.exists()

	override val length: Long
		get() = documentFile!!.length()

	override fun create() {
		DocumentFileCompat.mkdirs(MainApplication.context, fullPath)!!
			.makeFile(MainApplication.context, fileName)
	}

	override fun deleteIfExists() {
		if (exists && !documentFile!!.delete()) {
			throw IOException("Could not delete file $fullPath")
		}
	}

	override fun renameTo(fileName: String) {
		if (!documentFile!!.renameTo(fileName)) {
			throw IOException("Could not rename file $fullPath")
		}
		this.fileName = fileName
	}

	override fun createInputStream() =
		documentFile!!.openInputStream(MainApplication.context)!!

	override fun createOutputStream() =
		documentFile!!.openOutputStream(MainApplication.context)!!
}