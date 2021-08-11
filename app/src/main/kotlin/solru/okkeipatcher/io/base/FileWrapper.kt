package solru.okkeipatcher.io.base

import solru.okkeipatcher.core.base.ProgressProviderBase
import solru.okkeipatcher.io.services.base.IoService
import java.io.InputStream
import java.io.OutputStream

abstract class FileWrapper(
	val fullPath: String,
	fileName: String,
	protected val ioService: IoService
) : ProgressProviderBase() {

	var fileName: String = fileName
		protected set

	abstract val exists: Boolean
	abstract val length: Long
	abstract fun create()
	abstract fun deleteIfExists()
	abstract fun renameTo(fileName: String)
	abstract fun createInputStream(): InputStream
	abstract fun createOutputStream(): OutputStream

	suspend fun copyTo(destinationFile: FileWrapper) {
		destinationFile.deleteIfExists()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				ioService.copy(inputFile, outputFile, length, progressMutable)
			}
		}
	}

	suspend fun computeMd5() = createInputStream().use {
		ioService.computeHash(it, length, progressMutable)
	}

	suspend fun downloadFromUrl(url: String) {
		deleteIfExists()
		create()
		createOutputStream().use {
			ioService.download(url, it, progressMutable)
		}
	}
}