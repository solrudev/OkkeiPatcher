package solru.okkeipatcher.io.services.impl

import solru.okkeipatcher.io.services.base.*
import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class IoServiceImpl @Inject constructor(
	private val httpDownloader: HttpDownloader,
	private val streamCopier: StreamCopier,
	private val textReader: TextReader,
	private val textWriter: TextWriter
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

	override suspend fun readAllText(inputStream: InputStream) = textReader.readAllText(inputStream)

	override suspend fun writeAllText(outputStream: OutputStream, text: String) =
		textWriter.writeAllText(outputStream, text)
}