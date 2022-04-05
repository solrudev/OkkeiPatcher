package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.operation.Operation

interface Verifiable {
	fun verify(): Operation<Boolean>
}