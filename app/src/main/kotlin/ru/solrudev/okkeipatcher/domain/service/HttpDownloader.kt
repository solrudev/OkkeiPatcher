package ru.solrudev.okkeipatcher.domain.service

import ru.solrudev.okkeipatcher.domain.service.util.recreate
import java.io.File
import java.io.OutputStream

interface HttpDownloader {

	val progressMax: Int

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun download(
		url: String,
		outputStream: OutputStream,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit = {}
	): String
}

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return File hash. Empty string if [hashing] is `false`.
 */
suspend inline fun HttpDownloader.download(
	url: String,
	outputFile: File,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit
): String {
	outputFile.recreate()
	outputFile.outputStream().use { outputStream ->
		return download(url, outputStream, hashing, onProgressDeltaChanged)
	}
}