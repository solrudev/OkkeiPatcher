package solru.okkeipatcher.io.services.impl

import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.io.services.IoService
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class IoServiceImpl @Inject constructor(
	private val httpDownloader: HttpDownloader,
	private val streamCopier: StreamCopier
) : IoService {

	override suspend fun download(
		url: String,
		outputStream: OutputStream,
		hashing: Boolean,
		onProgressChanged: suspend (ProgressData) -> Unit
	) = httpDownloader.download(url, outputStream, hashing, onProgressChanged)

	override suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		hashing: Boolean,
		onProgressChanged: suspend (ProgressData) -> Unit
	) = streamCopier.copy(inputStream, outputStream, size, hashing, onProgressChanged)
}