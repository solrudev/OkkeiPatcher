package ru.solrudev.okkeipatcher.io.file

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.util.Preferences

abstract class VerifiableFile(private val fileImplementation: File) : File by fileImplementation, Verifiable {

	protected fun compareBySharedPreferences(key: String): Operation<Boolean> {
		val computeHashOperation = computeHash()
		return operation(computeHashOperation) {
			if (!exists) {
				return@operation false
			}
			val hashToCompare = Preferences.get(key, "")
			if (hashToCompare.isEmpty() || hashToCompare.isBlank()) {
				return@operation false
			}
			val hash = computeHashOperation()
			hash == hashToCompare
		}
	}

	protected fun compareByFile(file: File): Operation<Boolean> {
		val computeHashOperation = computeHash()
		val computeHashToCompareOperation = file.computeHash()
		return operation(computeHashOperation, computeHashToCompareOperation) {
			if (!exists) {
				return@operation false
			}
			if (!file.exists) {
				return@operation false
			}
			val hash = computeHashOperation()
			val hashToCompare = computeHashToCompareOperation()
			hash == hashToCompare
		}
	}
}