package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.*
import ru.solrudev.okkeipatcher.data.service.util.calculateProgress
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import java.io.File
import javax.inject.Inject
import kotlin.io.use

private const val BUFFER_LENGTH = 8192L

interface StreamCopier {

	val progressMax: Int

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun copy(
		source: Source,
		sink: Sink,
		size: Long,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit = {}
	): String
}

suspend inline fun StreamCopier.copy(
	inputFile: File,
	outputFile: File,
	ioDispatcher: CoroutineDispatcher,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit = {}
): String {
	withContext(ioDispatcher) { inputFile.source() }.use { source ->
		val sink = withContext(ioDispatcher) {
			outputFile.recreate()
			outputFile.sink()
		}
		return copy(source, sink, inputFile.length(), hashing, onProgressDeltaChanged)
	}
}

suspend inline fun StreamCopier.computeHash(
	file: File,
	ioDispatcher: CoroutineDispatcher,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit = {}
): String {
	val source = withContext(ioDispatcher) { file.source() }
	return copy(source, blackholeSink(), file.length(), hashing = true, onProgressDeltaChanged)
}

class StreamCopierImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : StreamCopier {

	override val progressMax = 100

	override suspend fun copy(
		source: Source,
		sink: Sink,
		size: Long,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	) = withContext(ioDispatcher) {
		val (progressDelta, progressRatio) = calculateProgress(size, BUFFER_LENGTH, progressMax)
		val finalSource = if (source !is BufferedSource) source.buffer() else source
		finalSource.use { source ->
			val finalSink = if (hashing) HashingSink.sha256(sink) else sink
			finalSink.buffer().use { bufferedSink ->
				Buffer().use { buffer ->
					var currentProgress = 0
					var accumulatedBytesRead = 0L
					while (true) {
						ensureActive()
						val bytesRead = source.read(buffer, BUFFER_LENGTH - accumulatedBytesRead)
						if (bytesRead < 0) {
							break
						}
						accumulatedBytesRead += bytesRead
						bufferedSink.write(buffer, buffer.size)
						if (accumulatedBytesRead == BUFFER_LENGTH) {
							accumulatedBytesRead = 0
							if (++currentProgress % progressRatio == 0) {
								onProgressDeltaChanged(progressDelta)
							}
						}
					}
					onProgressDeltaChanged(progressMax - currentProgress / progressRatio)
					bufferedSink.flush()
					return@withContext if (finalSink is HashingSink) finalSink.hash.hex() else ""
				}
			}
		}
	}
}