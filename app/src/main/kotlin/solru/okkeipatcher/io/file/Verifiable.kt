package solru.okkeipatcher.io.file

import solru.okkeipatcher.domain.operation.Operation

interface Verifiable {
	fun verify(): Operation<Boolean>
}