package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.core.operation.Operation

interface Verifiable {
	fun verify(): Operation<Boolean>
}