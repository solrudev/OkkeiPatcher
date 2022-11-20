package ru.solrudev.okkeipatcher.data.util

import okio.blackholeSink
import okio.source
import java.io.File

fun File.recreate() {
	delete()
	parentFile?.mkdirs()
	createNewFile()
}

inline fun File.computeHash(onProgressDeltaChanged: (Int) -> Unit = {}): String {
	return source().copyTo(blackholeSink(), length(), hashing = true, onProgressDeltaChanged)
}