package solru.okkeipatcher.io.services.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import solru.okkeipatcher.io.services.base.TextReader
import java.io.InputStream
import javax.inject.Inject

class TextReaderImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
	TextReader {
	override suspend fun readAllText(inputStream: InputStream) = withContext(ioDispatcher) {
		inputStream.source().buffer().use {
			it.readUtf8()
		}
	}
}