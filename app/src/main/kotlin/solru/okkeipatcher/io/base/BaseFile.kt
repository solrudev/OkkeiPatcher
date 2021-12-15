package solru.okkeipatcher.io.base

import solru.okkeipatcher.core.base.ProgressProvider
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.services.base.IoService

abstract class BaseFile(
	private val ioService: IoService,
	private val progressProvider: ProgressProviderImpl
) : File, ProgressProvider by progressProvider {

	override suspend fun computeHash() = createInputStream().use {
		ioService.computeHash(it, length, progressProvider.mutableProgress)
	}

	override suspend fun copyTo(destinationFile: File) {
		destinationFile.delete()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				ioService.copy(inputFile, outputFile, length, progressProvider.mutableProgress)
			}
		}
	}

	override suspend fun downloadFrom(url: String) {
		delete()
		create()
		createOutputStream().use {
			ioService.download(url, it, progressProvider.mutableProgress)
		}
	}
}