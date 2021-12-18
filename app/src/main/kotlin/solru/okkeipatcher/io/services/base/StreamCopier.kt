package solru.okkeipatcher.io.services.base

import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream

interface StreamCopier {
	suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		onProgressChanged: suspend (ProgressData) -> Unit
	)
}