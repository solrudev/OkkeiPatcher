package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import ru.solrudev.okkeipatcher.domain.service.computeHash

abstract class AbstractFile(private val streamCopier: StreamCopier) : File {

	override fun computeHash() = operation(progressMax = streamCopier.progressMax) {
		createInputStream().use {
			streamCopier.computeHash(it, length, ::progressDelta)
		}
	}

	override fun copyTo(destinationFile: File, hashing: Boolean) = operation(progressMax = streamCopier.progressMax) {
		destinationFile.delete()
		destinationFile.create()
		val input = createInputStream()
		val output = destinationFile.createOutputStream()
		streamCopier.copy(input, output, length, hashing, ::progressDelta)
	}
}