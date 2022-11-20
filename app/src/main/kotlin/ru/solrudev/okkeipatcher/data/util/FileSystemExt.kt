package ru.solrudev.okkeipatcher.data.util

import okio.FileSystem
import okio.Path
import okio.blackholeSink

fun FileSystem.prepareRecreate(path: Path) {
	delete(path)
	path.parent?.let(::createDirectories)
}

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return Output hash. Empty string if [hashing] is `false`.
 */
inline fun FileSystem.copy(
	source: Path,
	target: Path,
	hashing: Boolean = false,
	onProgressDeltaChanged: (Int) -> Unit = {}
): String {
	source(source).use { input ->
		val size = metadata(source).size ?: 0
		prepareRecreate(target)
		val sink = sink(target)
		return input.copyTo(sink, size, hashing, onProgressDeltaChanged)
	}
}

inline fun FileSystem.computeHash(path: Path, onProgressDeltaChanged: (Int) -> Unit = {}): String {
	val size = metadata(path).size ?: 0
	return source(path).copyTo(blackholeSink(), size, hashing = true, onProgressDeltaChanged)
}