package solru.okkeipatcher.io.services.base

import java.io.OutputStream

interface TextWriter {
	suspend fun writeAllText(outputStream: OutputStream, text: String)
}