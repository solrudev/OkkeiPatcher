package solru.okkeipatcher.io.base

import solru.okkeipatcher.core.base.ProgressProvider
import java.io.InputStream
import java.io.OutputStream

interface File : ProgressProvider {
	val name: String
	val fullPath: String
	val exists: Boolean
	val length: Long
	fun create()
	fun delete()
	fun renameTo(fileName: String)
	fun createInputStream(): InputStream
	fun createOutputStream(): OutputStream
	suspend fun computeHash(): String
	suspend fun copyTo(destinationFile: File)
	suspend fun downloadFrom(url: String)
}