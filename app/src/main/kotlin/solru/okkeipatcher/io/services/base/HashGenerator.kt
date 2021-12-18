package solru.okkeipatcher.io.services.base

import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream

interface HashGenerator {
	suspend fun computeHash(
		inputStream: InputStream,
		size: Long,
		onProgressChanged: suspend (ProgressData) -> Unit
	): String
}