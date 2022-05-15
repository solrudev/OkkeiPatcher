package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import java.io.InputStream
import java.io.OutputStream

interface File {
	val name: String
	val fullPath: String
	val exists: Boolean
	val length: Long
	fun create()
	fun delete()
	fun renameTo(fileName: String)
	fun createInputStream(): InputStream
	fun createOutputStream(): OutputStream
	fun computeHash(): Operation<String>

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	fun copyTo(destinationFile: File, hashing: Boolean = false): Operation<String>
}