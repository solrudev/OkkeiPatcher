package solru.okkeipatcher.io.services.base

import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.model.dto.ProgressData
import java.io.InputStream
import java.io.OutputStream

interface IoService {

	suspend fun download(
		url: String,
		outputStream: OutputStream,
		progress: MutableSharedFlow<ProgressData>
	)

	suspend fun computeHash(
		inputStream: InputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	): String

	suspend fun copy(
		inputStream: InputStream,
		outputStream: OutputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	)

	suspend fun readAllText(inputStream: InputStream): String
	suspend fun writeAllText(outputStream: OutputStream, text: String)
}