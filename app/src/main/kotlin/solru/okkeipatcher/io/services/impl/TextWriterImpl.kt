package solru.okkeipatcher.io.services.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import solru.okkeipatcher.io.services.base.TextWriter
import java.io.OutputStream
import javax.inject.Inject

class TextWriterImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) : TextWriter {

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun writeAllText(outputStream: OutputStream, text: String) {
		withContext(ioDispatcher) {
			outputStream.sink().buffer().use {
				it.writeUtf8(text)
			}
		}
	}
}