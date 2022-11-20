package ru.solrudev.okkeipatcher.data.util

import okio.*
import ru.solrudev.okkeipatcher.data.service.util.calculateProgress
import kotlin.io.use

const val BUFFER_LENGTH = 8192L
const val STREAM_COPY_PROGRESS_MAX = 100

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return Output hash. Empty string if [hashing] is `false`.
 */
inline fun Source.copyTo(
	sink: Sink,
	size: Long,
	hashing: Boolean = false,
	onProgressDeltaChanged: (Int) -> Unit = {}
): String {
	val (progressDelta, progressRatio) = calculateProgress(size, BUFFER_LENGTH, STREAM_COPY_PROGRESS_MAX)
	val finalSource = if (this !is BufferedSource) buffer() else this
	finalSource.use { source ->
		val finalSink = if (hashing) HashingSink.sha256(sink) else sink
		finalSink.buffer().use { bufferedSink ->
			Buffer().use { buffer ->
				var currentProgress = 0
				var accumulatedBytesRead = 0L
				while (true) {
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
				onProgressDeltaChanged(STREAM_COPY_PROGRESS_MAX - currentProgress / progressRatio)
				bufferedSink.flush()
				return if (finalSink is HashingSink) finalSink.hash.hex() else ""
			}
		}
	}
}