package solru.okkeipatcher.io.utils

import java.io.File
import java.io.OutputStream
import kotlin.math.ceil

private const val EMIT_COUNT = 400

fun calculateProgressRatio(totalSize: Long, bufferLength: Long) =
	ceil(totalSize.toDouble() / (bufferLength * EMIT_COUNT)).toInt().coerceAtLeast(1)

fun deleteTempZipFiles(directory: File?) {
	if (directory == null || !directory.isDirectory) return
	directory.listFiles()
		?.filter { it.extension.matches(Regex("(apk|zip)\\d+")) }
		?.forEach { it.delete() }
}

/**
 * An [OutputStream] which writes nowhere.
 */
class BlackholeOutputStream : OutputStream() {
	override fun write(b: Int) {}
	override fun write(b: ByteArray?) {}
	override fun write(b: ByteArray?, off: Int, len: Int) {}
}