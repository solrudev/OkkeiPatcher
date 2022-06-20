package ru.solrudev.okkeipatcher.io.util

import java.io.OutputStream

/**
 * An [OutputStream] which writes nowhere.
 */
object BlackholeOutputStream : OutputStream() {
	override fun write(b: Int) {}
	override fun write(b: ByteArray?) {}
	override fun write(b: ByteArray?, off: Int, len: Int) {}
}