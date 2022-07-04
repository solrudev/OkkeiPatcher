package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class JavaFile(private val file: File, streamCopier: StreamCopier) : AbstractFile(streamCopier) {

	override val name: String
		get() = file.name

	override val fullPath: String
		get() = file.absolutePath

	override val exists: Boolean
		get() = file.exists()

	override val length: Long
		get() = file.length()

	override fun create() {
		file.parentFile?.mkdirs()
		file.createNewFile()
	}

	override fun delete() {
		if (exists && !file.delete()) {
			throw IOException("Could not delete file $fullPath")
		}
	}

	override fun renameTo(fileName: String) {
		if (!file.renameTo(File(file.parent, fileName))) {
			throw IOException("Could not rename file $fullPath")
		}
	}

	override fun createInputStream() = FileInputStream(file)
	override fun createOutputStream() = FileOutputStream(file)
}