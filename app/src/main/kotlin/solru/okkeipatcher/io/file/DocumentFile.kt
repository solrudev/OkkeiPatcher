package solru.okkeipatcher.io.file

import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.makeFile
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.io.services.StreamCopier
import java.io.IOException

class DocumentFile(private val path: String, name: String, streamCopier: StreamCopier) : BaseFile(streamCopier) {

	private val documentFile: DocumentFile? by lazy {
		DocumentFileCompat.fromFullPath(
			OkkeiApplication.context,
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
		DocumentFileCompat.mkdirs(OkkeiApplication.context, path)!!.makeFile(OkkeiApplication.context, name)
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
		documentFile!!.openInputStream(OkkeiApplication.context)!!

	override fun createOutputStream() =
		documentFile!!.openOutputStream(OkkeiApplication.context)!!
}