package solru.okkeipatcher.io.file

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

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	suspend fun copyTo(destinationFile: File, hashing: Boolean = false): String

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	suspend fun downloadFrom(url: String, hashing: Boolean = false): String
}