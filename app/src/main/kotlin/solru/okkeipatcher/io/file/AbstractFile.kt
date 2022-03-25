package solru.okkeipatcher.io.file

import solru.okkeipatcher.domain.base.ProgressPublisher
import solru.okkeipatcher.domain.base.ProgressPublisherImpl
import solru.okkeipatcher.io.service.StreamCopier
import solru.okkeipatcher.io.util.BlackholeOutputStream

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