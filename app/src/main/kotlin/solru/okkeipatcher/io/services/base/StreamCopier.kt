package solru.okkeipatcher.io.services.base

import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream

interface StreamCopier {

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		hashing: Boolean = false,
		onProgressChanged: suspend (ProgressData) -> Unit
	): String
}