package solru.okkeipatcher.io.file

import solru.okkeipatcher.core.progress.ProgressPublisher
import solru.okkeipatcher.core.progress.ProgressPublisherImpl
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.io.utils.BlackholeOutputStream

abstract class BaseFile(
	private val streamCopier: StreamCopier,
	private val progressPublisher: ProgressPublisherImpl = ProgressPublisherImpl()
) : File, ProgressPublisher by progressPublisher {

	override suspend fun computeHash() = createInputStream().use {
		streamCopier.copy(it, BlackholeOutputStream(), length, hashing = true) { progressData ->
			progressPublisher.mutableProgress.emit(progressData)
		}
	}

	override suspend fun copyTo(destinationFile: File, hashing: Boolean): String {
		destinationFile.delete()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				return streamCopier.copy(inputFile, outputFile, length, hashing) { progressData ->
					progressPublisher.mutableProgress.emit(progressData)
				}
			}
		}
	}
}