package solru.okkeipatcher.io.services.base

import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream

interface HashGenerator {
	suspend fun computeHash(
		inputStream: InputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	): String
}