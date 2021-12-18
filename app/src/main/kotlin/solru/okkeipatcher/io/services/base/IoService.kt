package solru.okkeipatcher.io.services.base

import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream

interface IoService {

	suspend fun download(
		url: String,
		outputStream: OutputStream,
		onProgressChanged: suspend (ProgressData) -> Unit
	)

	suspend fun computeHash(
		inputStream: InputStream,
		size: Long,
		onProgressChanged: suspend (ProgressData) -> Unit
	): String

	suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		onProgressChanged: suspend (ProgressData) -> Unit
	)

	suspend fun readAllText(inputStream: InputStream): String
	suspend fun writeAllText(outputStream: OutputStream, text: String)
}