package solru.okkeipatcher.io.services.base

import solru.okkeipatcher.model.dto.ProgressData
import java.io.OutputStream

interface HttpDownloader {

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun download(
		url: String,
		outputStream: OutputStream,
		hashing: Boolean = false,
		onProgressChanged: suspend (ProgressData) -> Unit
	): String
}