package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import ru.solrudev.okkeipatcher.io.util.BlackholeOutputStream

abstract class AbstractFile(private val streamCopier: StreamCopier) : File {

	override fun computeHash() = object : AbstractOperation<String>() {

		override val progressMax = 100

		override suspend fun invoke() = createInputStream().use {
			streamCopier.copy(it, BlackholeOutputStream(), length, hashing = true) { progressDelta ->
				emitProgressDelta(progressDelta)
			}
		}
	}

	override fun copyTo(destinationFile: File, hashing: Boolean) = object : AbstractOperation<String>() {

		override val progressMax = 100

		override suspend fun invoke(): String {
			destinationFile.delete()
			destinationFile.create()
			createInputStream().use { inputFile ->
				destinationFile.createOutputStream().use { outputFile ->
					return streamCopier.copy(inputFile, outputFile, length, hashing) { progressDelta ->
						emitProgressDelta(progressDelta)
					}
				}
			}
		}
	}
}