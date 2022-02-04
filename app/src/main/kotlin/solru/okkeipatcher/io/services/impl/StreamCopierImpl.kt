package solru.okkeipatcher.io.services.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.*
import okio.HashingSink.Companion.sha256
import solru.okkeipatcher.data.ProgressData
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.io.utils.BlackholeOutputStream
import solru.okkeipatcher.io.utils.calculateProgressRatio
import solru.okkeipatcher.utils.extensions.empty
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil

private const val BUFFER_LENGTH = 8192L

class StreamCopierImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : StreamCopier {

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		hashing: Boolean,
		onProgressChanged: suspend (ProgressData) -> Unit
	) = withContext(ioDispatcher) {
		onProgressChanged(ProgressData())
		val progressRatio = calculateProgressRatio(size, BUFFER_LENGTH)
		inputStream.source().buffer().use { source ->
			val baseSink = if (outputStream is BlackholeOutputStream) blackholeSink() else outputStream.sink()
			val sink = if (hashing) sha256(baseSink) else baseSink
			sink.buffer().use { bufferedSink ->
				val progressMax = ceil(size.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
				var currentProgress = 0
				Buffer().use { buffer ->
					while (source.read(buffer, BUFFER_LENGTH) > 0) {
						ensureActive()
						bufferedSink.write(buffer, buffer.size)
						currentProgress++
						if (currentProgress % progressRatio == 0) {
							onProgressChanged(ProgressData(currentProgress / progressRatio, progressMax))
						}
					}
					bufferedSink.flush()
					if (sink is HashingSink) sink.hash.hex() else String.empty
				}
			}
		}
	}
}