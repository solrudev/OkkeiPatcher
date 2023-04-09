/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.util

import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import kotlin.math.ceil
import kotlin.math.roundToInt

const val BUFFER_LENGTH = 8192L
const val STREAM_COPY_PROGRESS_MAX = 100

inline fun BufferedSource.copyTo(sink: BufferedSink, size: Long, onProgressDeltaChanged: (Int) -> Unit = {}) {
	val (progressDelta, progressRatio) = calculateProgress(size, BUFFER_LENGTH, STREAM_COPY_PROGRESS_MAX)
	Buffer().use { buffer ->
		var currentProgress = 0
		var accumulatedBytesRead = 0L
		while (true) {
			val bytesRead = read(buffer, BUFFER_LENGTH - accumulatedBytesRead)
			if (bytesRead < 0) {
				break
			}
			accumulatedBytesRead += bytesRead
			sink.write(buffer, buffer.size)
			if (accumulatedBytesRead == BUFFER_LENGTH) {
				accumulatedBytesRead = 0
				if (++currentProgress % progressRatio == 0) {
					onProgressDeltaChanged(progressDelta)
				}
			}
		}
		onProgressDeltaChanged(STREAM_COPY_PROGRESS_MAX - currentProgress / progressRatio)
	}
}

data class Progress(
	val progressDelta: Int,
	val progressRatio: Int
)

fun calculateProgress(totalSize: Long, bufferLength: Long, progressMax: Int): Progress {
	val ratio = ceil(totalSize.toDouble() / (bufferLength * progressMax)).toInt().coerceAtLeast(1)
	val max = ceil(totalSize.toDouble() / (bufferLength * ratio)).toInt()
	val delta = (progressMax.toDouble() / max).roundToInt()
	return Progress(delta, ratio)
}