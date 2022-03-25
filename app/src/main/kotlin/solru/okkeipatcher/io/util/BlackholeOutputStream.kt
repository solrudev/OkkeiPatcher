package solru.okkeipatcher.io.util

import java.io.OutputStream

/**
 * An [OutputStream] which writes nowhere.
 */
class BlackholeOutputStream : OutputStream() {
	override fun write(b: Int) {}
	override fun write(b: ByteArray?) {}
	override fun write(b: ByteArray?, off: Int, len: Int) {}
}