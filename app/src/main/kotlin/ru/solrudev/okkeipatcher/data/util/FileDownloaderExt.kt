package ru.solrudev.okkeipatcher.data.util

import okio.sink
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import java.io.File

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return File hash. Empty string if [hashing] is `false`.
 */
suspend inline fun FileDownloader.download(
	url: String,
	outputFile: File,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit
): String {
	outputFile.recreate()
	return download(url, outputFile.sink(), hashing, onProgressDeltaChanged)
}