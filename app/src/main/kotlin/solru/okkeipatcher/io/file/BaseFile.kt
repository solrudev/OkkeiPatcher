package solru.okkeipatcher.io.file

import solru.okkeipatcher.core.base.ProgressProvider
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.BlackholeOutputStream

abstract class BaseFile(
	private val ioService: IoService,
	private val progressProvider: ProgressProviderImpl
) : File, ProgressProvider by progressProvider {

	override suspend fun computeHash() = createInputStream().use {
		ioService.copy(it, BlackholeOutputStream(), length, hashing = true) { progressData ->
			progressProvider.mutableProgress.emit(progressData)
		}
	}

	override suspend fun copyTo(destinationFile: File, hashing: Boolean): String {
		destinationFile.delete()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				return ioService.copy(inputFile, outputFile, length, hashing) { progressData ->
					progressProvider.mutableProgress.emit(progressData)
				}
			}
		}
	}

	override suspend fun downloadFrom(url: String, hashing: Boolean): String {
		delete()
		create()
		createOutputStream().use {
			return ioService.download(url, it, hashing) { progressData ->
				progressProvider.mutableProgress.emit(progressData)
			}
		}
	}
}