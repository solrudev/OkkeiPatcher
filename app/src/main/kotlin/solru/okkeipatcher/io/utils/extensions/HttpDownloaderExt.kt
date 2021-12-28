package solru.okkeipatcher.io.utils.extensions

import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.io.services.HttpDownloader
import java.io.File
import java.io.FileOutputStream

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return File hash. Empty string if [hashing] is `false`.
 */
suspend inline fun HttpDownloader.download(
	url: String,
	outputFile: File,
	hashing: Boolean = false,
	noinline onProgressChanged: suspend (ProgressData) -> Unit
): String {
	if (outputFile.exists()) outputFile.delete()
	outputFile.parentFile?.mkdirs()
	outputFile.createNewFile()
	FileOutputStream(outputFile).use { outputStream ->
		return download(url, outputStream, hashing, onProgressChanged)
	}
}