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

import okio.FileSystem
import okio.HashingSink
import okio.HashingSink.Companion.sha256
import okio.Path
import okio.blackholeSink
import okio.buffer
import ru.solrudev.okkeipatcher.domain.util.prepareRecreate

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return Output hash. Empty string if [hashing] is `false`.
 */
inline fun FileSystem.copy(
	source: Path,
	target: Path,
	hashing: Boolean = false,
	onProgressChanged: (Int) -> Unit = {}
): String {
	val size = metadata(source).size ?: 0
	source(source).buffer().use { bufferedSource ->
		prepareRecreate(target)
		val sink = if (hashing) sha256(sink(target)) else sink(target)
		sink.buffer().use { bufferedSink ->
			bufferedSource.copyTo(bufferedSink, size, onProgressChanged)
		}
		return if (sink is HashingSink) sink.hash.hex() else ""
	}
}

inline fun FileSystem.computeHash(path: Path, onProgressChanged: (Int) -> Unit = {}): String {
	val size = metadata(path).size ?: 0
	source(path).buffer().use { source ->
		val hashingSink = sha256(blackholeSink())
		hashingSink.buffer().use { sink ->
			source.copyTo(sink, size, onProgressChanged)
		}
		return hashingSink.hash.hex()
	}
}