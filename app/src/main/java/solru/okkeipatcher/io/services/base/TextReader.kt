package solru.okkeipatcher.io.services.base

import java.io.InputStream

interface TextReader {
	suspend fun readAllText(inputStream: InputStream): String
}