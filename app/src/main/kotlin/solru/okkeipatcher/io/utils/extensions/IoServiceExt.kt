package solru.okkeipatcher.io.utils.extensions

import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.model.dto.ProgressData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

suspend inline fun IoService.download(
	url: String,
	outputFile: File,
	noinline onProgressChanged: suspend (ProgressData) -> Unit
) {
	if (outputFile.exists()) outputFile.delete()
	outputFile.parentFile?.mkdirs()
	outputFile.createNewFile()
	FileOutputStream(outputFile).use { outputStream ->
		download(url, outputStream, onProgressChanged)
	}
}

suspend inline fun IoService.computeHash(
	inputFile: File,
	noinline onProgressChanged: suspend (ProgressData) -> Unit
) = FileInputStream(inputFile).use {
	computeHash(it, inputFile.length(), onProgressChanged)
}

suspend inline fun IoService.copyFile(
	inputFile: File,
	outputFile: File,
	noinline onProgressChanged: suspend (ProgressData) -> Unit
) {
	if (outputFile.exists()) outputFile.delete()
	outputFile.parentFile?.mkdirs()
	outputFile.createNewFile()
	FileInputStream(inputFile).use { inputStream ->
		FileOutputStream(outputFile).use { outputStream ->
			copy(inputStream, outputStream, inputFile.length(), onProgressChanged)
		}
	}
}

suspend inline fun IoService.readAllText(file: File) =
	FileInputStream(file).use { inputStream ->
		return@use readAllText(inputStream)
	}