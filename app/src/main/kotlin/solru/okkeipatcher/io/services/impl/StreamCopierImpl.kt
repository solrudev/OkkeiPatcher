package solru.okkeipatcher.io.services.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.buffer
import okio.sink
import okio.source
import solru.okkeipatcher.io.services.base.StreamCopier
import solru.okkeipatcher.io.utils.calculateProgressRatio
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.reset
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlin.math.ceil

private const val BUFFER_LENGTH: Long = 8192

class StreamCopierImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
	StreamCopier {

	override suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	) = withContext(ioDispatcher) {
		progress.reset()
		val progressRatio = calculateProgressRatio(size, BUFFER_LENGTH)
		inputStream.source().buffer().use { source ->
			outputStream.sink().buffer().use { sink ->
				val progressMax =
					ceil(size.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
				var currentProgress = 0
				Buffer().use { buffer ->
					while (source.read(buffer, BUFFER_LENGTH) > 0) {
						ensureActive()
						sink.write(buffer, buffer.size)
						++currentProgress
						if (currentProgress % progressRatio == 0) {
							progress.emit(currentProgress / progressRatio, progressMax)
						}
					}
				}
			}
		}
	}
}