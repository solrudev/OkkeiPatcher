package ru.solrudev.okkeipatcher.io.file

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.withIndex
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.util.Preferences

abstract class VerifiableFile(private val fileImplementation: File) : File by fileImplementation, Verifiable {

	protected fun compareBySharedPreferences(key: String) = object : AbstractOperation<Boolean>() {

		private val computeHashOperation = computeHash()
		override val progressDelta = merge(computeHashOperation.progressDelta, _progressDelta)
		override val progressMax = 100

		override suspend fun invoke(): Boolean {
			if (!exists) {
				_progressDelta.emit(progressMax)
				return false
			}
			val hashToCompare = Preferences.get(key, "")
			if (hashToCompare.isEmpty() || hashToCompare.isBlank()) {
				_progressDelta.emit(progressMax)
				return false
			}
			val hash = computeHashOperation()
			return hash == hashToCompare
		}
	}

	protected fun compareByFile(file: File) = object : AbstractOperation<Boolean>() {

		private val computeHashOperation = computeHash()
		private val computeHashToCompareOperation = file.computeHash()

		override val progressDelta = merge(
			computeHashOperation.progressDelta
				.withIndex()
				.filter { indexedValue -> indexedValue.index % 2 == 0 }
				.map { it.value },
			computeHashToCompareOperation.progressDelta
				.withIndex()
				.filter { indexedValue -> indexedValue.index % 2 == 0 }
				.map { it.value },
			_progressDelta
		)

		override val progressMax = 100

		override suspend fun invoke(): Boolean {
			if (!exists) {
				_progressDelta.emit(progressMax)
				return false
			}
			if (!file.exists) {
				_progressDelta.emit(progressMax)
				return false
			}
			val hash = computeHashOperation()
			val hashToCompare = computeHashToCompareOperation()
			return hash == hashToCompare
		}
	}
}