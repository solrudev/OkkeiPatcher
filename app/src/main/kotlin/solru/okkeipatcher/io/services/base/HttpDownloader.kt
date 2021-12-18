package solru.okkeipatcher.io.services.base

import solru.okkeipatcher.model.dto.ProgressData
import java.io.OutputStream

interface HttpDownloader {
	suspend fun download(
		url: String,
		outputStream: OutputStream,
		onProgressChanged: suspend (ProgressData) -> Unit
	)
}