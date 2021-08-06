package solru.okkeipatcher.io.utils.extensions

import kotlinx.coroutines.flow.MutableSharedFlow
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.model.dto.ProgressData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

suspend inline fun IoService.download(
	url: String,
	outputFile: File,
	progress: MutableSharedFlow<ProgressData>
) {
	if (outputFile.exists()) outputFile.delete()
	outputFile.parentFile?.mkdirs()
	outputFile.createNewFile()
	FileOutputStream(outputFile).use { outputStream ->
		download(url, outputStream, progress)
	}
}

suspend inline fun IoService.computeHash(
	inputFile: File,
	progress: MutableSharedFlow<ProgressData>
) = FileInputStream(inputFile).use {
	computeHash(it, inputFile.length(), progress)
}

suspend inline fun IoService.copyFile(
	inputFile: File,
	outputFile: File,
	progress: MutableSharedFlow<ProgressData>
) {
	if (outputFile.exists()) outputFile.delete()
	outputFile.parentFile?.mkdirs()
	outputFile.createNewFile()
	FileInputStream(inputFile).use { inputStream ->
		FileOutputStream(outputFile).use { outputStream ->
			copy(inputStream, outputStream, inputFile.length(), progress)
		}
	}
}

suspend inline fun IoService.readAllText(file: File) =
	FileInputStream(file).use { inputStream ->
		return@use readAllText(inputStream)
	}