package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.*
import ru.solrudev.okkeipatcher.data.service.util.calculateProgressRatio
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import java.io.File
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil
import kotlin.math.roundToInt

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

@Suppress("BlockingMethodInNonBlockingContext")
suspend inline fun StreamCopier.copy(
	inputFile: File,
	outputFile: File,
	ioDispatcher: CoroutineDispatcher,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit = {}
): String {
	outputFile.recreate()
	withContext(ioDispatcher) { inputFile.source() }.use { source ->
		val sink = withContext(ioDispatcher) { outputFile.sink() }
		return copy(source, sink, inputFile.length(), hashing, onProgressDeltaChanged)
	}
}

@Suppress("BlockingMethodInNonBlockingContext")
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

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun copy(
		source: Source,
		sink: Sink,
		size: Long,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	) = withContext(ioDispatcher) {
		val progressRatio = calculateProgressRatio(size, BUFFER_LENGTH)
		source.buffer().use { source ->
			val finalSink = if (hashing) HashingSink.sha256(sink) else sink
			finalSink.buffer().use { bufferedSink ->
				val progressMax = ceil(size.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
				val progressDelta = (100.0 / progressMax).roundToInt()
				var currentProgress = 0
				Buffer().use { buffer ->
					while (source.read(buffer, BUFFER_LENGTH) > 0) {
						ensureActive()
						bufferedSink.write(buffer, buffer.size)
						currentProgress++
						if (currentProgress % progressRatio == 0) {
							onProgressDeltaChanged(progressDelta)
						}
					}
					onProgressDeltaChanged(100 - currentProgress / progressRatio)
					bufferedSink.flush()
					if (finalSink is HashingSink) finalSink.hash.hex() else ""
				}
			}
		}
	}
}