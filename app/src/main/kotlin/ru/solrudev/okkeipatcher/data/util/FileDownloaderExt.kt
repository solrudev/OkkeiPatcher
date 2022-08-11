package ru.solrudev.okkeipatcher.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.sink
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import java.io.File

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return File hash. Empty string if [hashing] is `false`.
 */
@Suppress("BlockingMethodInNonBlockingContext")
suspend inline fun FileDownloader.download(
	url: String,
	outputFile: File,
	ioDispatcher: CoroutineDispatcher,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit
): String {
	outputFile.recreate()
	val sink = withContext(ioDispatcher) { outputFile.sink() }
	return download(url, sink, hashing, onProgressDeltaChanged)
}