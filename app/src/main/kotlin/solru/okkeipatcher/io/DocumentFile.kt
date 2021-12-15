package solru.okkeipatcher.io

import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.makeFile
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import solru.okkeipatcher.MainApplication
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.base.BaseFile
import solru.okkeipatcher.io.services.base.IoService
import java.io.IOException

class DocumentFile(private val path: String, name: String, ioService: IoService) :
	BaseFile(ioService, ProgressProviderImpl()) {

	private val documentFile: DocumentFile? by lazy {
		DocumentFileCompat.fromFullPath(
			MainApplication.context,
			"$path/$name"
		)
	}

	override val fullPath: String
		get() = "$path/$name"

	override val name: String
		get() = documentFile!!.name!!

	override val exists: Boolean
		get() = documentFile!!.exists()

	override val length: Long
		get() = documentFile!!.length()

	override fun create() {
		DocumentFileCompat.mkdirs(MainApplication.context, path)!!.makeFile(MainApplication.context, name)
	}

	override fun delete() {
		if (exists && !documentFile!!.delete()) {
			throw IOException("Could not delete file $path/$name")
		}
	}

	override fun renameTo(fileName: String) {
		if (!documentFile!!.renameTo(fileName)) {
			throw IOException("Could not rename file $path/$name")
		}
	}

	override fun createInputStream() =
		documentFile!!.openInputStream(MainApplication.context)!!

	override fun createOutputStream() =
		documentFile!!.openOutputStream(MainApplication.context)!!
}