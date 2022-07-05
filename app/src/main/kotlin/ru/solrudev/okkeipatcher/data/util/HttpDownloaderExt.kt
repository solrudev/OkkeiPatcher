package ru.solrudev.okkeipatcher.data.util

import ru.solrudev.okkeipatcher.domain.service.HttpDownloader
import java.io.File

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