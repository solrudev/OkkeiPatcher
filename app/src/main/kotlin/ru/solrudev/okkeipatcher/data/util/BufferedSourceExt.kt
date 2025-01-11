/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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
import ru.solrudev.okkeipatcher.domain.util.DEFAULT_PROGRESS_MAX
import kotlin.math.roundToInt

const val BUFFER_LENGTH = 8192L

inline fun BufferedSource.copyTo(sink: BufferedSink, size: Long, onProgressChanged: (Int) -> Unit = {}) {
	val progressRatio = calculateProgressRatio(size, BUFFER_LENGTH, DEFAULT_PROGRESS_MAX)
	Buffer().use { buffer ->
		var currentProgress = 0
		var accumulatedBytesRead = 0L
		var progressEmitCounter = 0
		while (true) {
			val bytesRead = read(buffer, byteCount = BUFFER_LENGTH - accumulatedBytesRead)
			if (bytesRead < 0) {
				break
			}
			accumulatedBytesRead += bytesRead
			sink.write(buffer, buffer.size)
			if (accumulatedBytesRead == BUFFER_LENGTH) {
				accumulatedBytesRead = 0
				val progress = ++currentProgress / progressRatio
				val shouldEmitProgress = currentProgress - (progress * progressRatio) == 0
				if (shouldEmitProgress && progress <= DEFAULT_PROGRESS_MAX) {
					progressEmitCounter++
					onProgressChanged(1)
				}
			}
		}
		onProgressChanged(DEFAULT_PROGRESS_MAX - progressEmitCounter)
	}
}

fun calculateProgressRatio(totalSize: Long, bufferLength: Long, progressMax: Int): Int {
	return (totalSize.toDouble() / (bufferLength * progressMax)).roundToInt().coerceAtLeast(1)
}