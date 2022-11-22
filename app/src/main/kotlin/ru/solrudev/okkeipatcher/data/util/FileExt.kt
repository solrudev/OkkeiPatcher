package ru.solrudev.okkeipatcher.data.util

import okio.HashingSink.Companion.sha256
import okio.blackholeSink
import okio.buffer
import okio.source
import java.io.File

fun File.recreate() {
	delete()
	parentFile?.mkdirs()
	createNewFile()
}

inline fun File.computeHash(onProgressDeltaChanged: (Int) -> Unit = {}): String {
	source().buffer().use { source ->
		val hashingSink = sha256(blackholeSink())
		hashingSink.buffer().use { sink ->
			source.copyTo(sink, length(), onProgressDeltaChanged)
			return hashingSink.hash.hex()
		}
	}
}