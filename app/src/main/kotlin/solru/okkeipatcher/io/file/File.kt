package solru.okkeipatcher.io.file

import solru.okkeipatcher.domain.progress.ProgressPublisher
import java.io.InputStream
import java.io.OutputStream

interface File : ProgressPublisher {
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

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	suspend fun copyTo(destinationFile: File, hashing: Boolean = false): String
}