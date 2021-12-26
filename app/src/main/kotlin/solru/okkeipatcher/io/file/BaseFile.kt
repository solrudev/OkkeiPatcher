package solru.okkeipatcher.io.file

import solru.okkeipatcher.core.base.ProgressProvider
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.io.utils.BlackholeOutputStream

abstract class BaseFile(
	private val streamCopier: StreamCopier,
	private val progressProvider: ProgressProviderImpl = ProgressProviderImpl()
) : File, ProgressProvider by progressProvider {

	override suspend fun computeHash() = createInputStream().use {
		streamCopier.copy(it, BlackholeOutputStream(), length, hashing = true) { progressData ->
			progressProvider.mutableProgress.emit(progressData)
		}
	}

	override suspend fun copyTo(destinationFile: File, hashing: Boolean): String {
		destinationFile.delete()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				return streamCopier.copy(inputFile, outputFile, length, hashing) { progressData ->
					progressProvider.mutableProgress.emit(progressData)
				}
			}
		}
	}
}