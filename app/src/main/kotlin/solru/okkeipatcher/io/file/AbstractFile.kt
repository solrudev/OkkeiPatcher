package solru.okkeipatcher.io.file

import solru.okkeipatcher.domain.progress.ProgressPublisher
import solru.okkeipatcher.domain.progress.ProgressPublisherImpl
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.io.utils.BlackholeOutputStream

abstract class AbstractFile(
	private val streamCopier: StreamCopier,
	private val progressPublisher: ProgressPublisherImpl = ProgressPublisherImpl()
) : File, ProgressPublisher by progressPublisher {

	override suspend fun computeHash() = createInputStream().use {
		streamCopier.copy(it, BlackholeOutputStream(), length, hashing = true) { progressData ->
			progressPublisher._progress.emit(progressData)
		}
	}

	override suspend fun copyTo(destinationFile: File, hashing: Boolean): String {
		destinationFile.delete()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				return streamCopier.copy(inputFile, outputFile, length, hashing) { progressData ->
					progressPublisher._progress.emit(progressData)
				}
			}
		}
	}
}