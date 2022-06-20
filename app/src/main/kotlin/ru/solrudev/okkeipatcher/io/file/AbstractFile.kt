package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import ru.solrudev.okkeipatcher.io.service.computeHash

abstract class AbstractFile(private val streamCopier: StreamCopier) : File {

	override fun computeHash() = operation(progressMax = streamCopier.progressMax) {
		createInputStream().use {
			streamCopier.computeHash(it, length) { progressDelta ->
				progressDelta(progressDelta)
			}
		}
	}

	override fun copyTo(destinationFile: File, hashing: Boolean) = operation(progressMax = streamCopier.progressMax) {
		destinationFile.delete()
		destinationFile.create()
		createInputStream().use { inputFile ->
			destinationFile.createOutputStream().use { outputFile ->
				streamCopier.copy(inputFile, outputFile, length, hashing) { progressDelta ->
					progressDelta(progressDelta)
				}
			}
		}
	}
}