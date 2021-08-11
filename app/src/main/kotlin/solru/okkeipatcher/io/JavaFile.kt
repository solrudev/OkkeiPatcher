package solru.okkeipatcher.io

import solru.okkeipatcher.io.base.FileWrapper
import solru.okkeipatcher.io.services.base.IoService
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

open class JavaFile(private val file: File, ioService: IoService) :
	FileWrapper(file.absolutePath, file.name, ioService) {

	override val exists: Boolean
		get() = file.exists()

	override val length: Long
		get() = file.length()

	override fun create() {
		file.parentFile?.mkdirs()
		file.createNewFile()
	}

	override fun deleteIfExists() {
		if (exists && !file.delete()) {
			throw IOException("Could not delete file ${file.absolutePath}")
		}
	}

	override fun renameTo(fileName: String) {
		if (!file.renameTo(File(file.parent, fileName))) {
			throw IOException("Could not rename file ${file.absolutePath}")
		}
		this.fileName = fileName
	}

	override fun createInputStream() = FileInputStream(file)
	override fun createOutputStream() = FileOutputStream(file)
}