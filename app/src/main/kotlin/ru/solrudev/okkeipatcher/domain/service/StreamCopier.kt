package ru.solrudev.okkeipatcher.domain.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.*
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.service.util.BlackholeOutputStream
import ru.solrudev.okkeipatcher.domain.service.util.calculateProgressRatio
import ru.solrudev.okkeipatcher.domain.service.util.recreate
import java.io.File
import java.io.InputStream
import java.io.OutputStream
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
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit = {}
	): String
}

suspend inline fun StreamCopier.copy(
	inputFile: File,
	outputFile: File,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit = {}
): String {
	outputFile.recreate()
	return copy(inputFile.inputStream(), outputFile.outputStream(), inputFile.length(), hashing, onProgressDeltaChanged)
}

suspend inline fun StreamCopier.computeHash(
	inputStream: InputStream,
	size: Long,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit = {}
) = copy(inputStream, BlackholeOutputStream, size, hashing = true, onProgressDeltaChanged)

suspend inline fun StreamCopier.computeHash(
	file: File,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit = {}
) = computeHash(file.inputStream(), file.length(), onProgressDeltaChanged)

class StreamCopierImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : StreamCopier {

	override val progressMax = 100

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	) = withContext(ioDispatcher) {
		val progressRatio = calculateProgressRatio(size, BUFFER_LENGTH)
		inputStream.source().buffer().use { source ->
			val baseSink = if (outputStream is BlackholeOutputStream) blackholeSink() else outputStream.sink()
			val sink = if (hashing) HashingSink.sha256(baseSink) else baseSink
			sink.buffer().use { bufferedSink ->
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
					if (sink is HashingSink) sink.hash.hex() else ""
				}
			}
		}
	}
}