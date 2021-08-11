package solru.okkeipatcher.io.services.impl

import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.io.services.base.*
import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class IoServiceImpl @Inject constructor(
	private val httpDownloader: HttpDownloader,
	private val hashGenerator: HashGenerator,
	private val streamCopier: StreamCopier,
	private val textReader: TextReader,
	private val textWriter: TextWriter
) : IoService {

	override suspend fun download(
		url: String,
		outputStream: OutputStream,
		progress: MutableSharedFlow<ProgressData>
	) = httpDownloader.download(url, outputStream, progress)

	override suspend fun computeHash(
		inputStream: InputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	) = hashGenerator.computeHash(inputStream, size, progress)

	override suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	) = streamCopier.copy(inputStream, outputStream, size, progress)

	override suspend fun readAllText(inputStream: InputStream) = textReader.readAllText(inputStream)

	override suspend fun writeAllText(outputStream: OutputStream, text: String) =
		textWriter.writeAllText(outputStream, text)
}