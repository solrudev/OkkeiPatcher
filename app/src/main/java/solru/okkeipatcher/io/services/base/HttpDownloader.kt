package solru.okkeipatcher.io.services.base

import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.model.dto.ProgressData
import java.io.OutputStream

interface HttpDownloader {
	suspend fun download(
		url: String,
		outputStream: OutputStream,
		progress: MutableSharedFlow<ProgressData>
	)
}