package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.operation.AggregateOperation
import ru.solrudev.okkeipatcher.domain.operation.EmptyOperation
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy

class RestoreOperation(
	private val strategy: GameFileStrategy,
	private val handleSaveData: Boolean,
	private val preferencesRepository: PreferencesRepository // TODO: see PatchOperation
) : Operation<Unit> {

	private val operation = object : AggregateOperation(
		with(strategy) {
			listOf(
				if (handleSaveData) saveData.backup() else EmptyOperation,
				apk.restore(),
				obb.restore(),
				if (handleSaveData) saveData.restore() else EmptyOperation
			)
		}
	) {
		override suspend fun postInvoke() = strategy.run {
			apk.deleteBackup()
			obb.deleteBackup()
			preferencesRepository.setIsPatched(false)
		}
	}

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun invoke() = strategy.use { operation() }

	/**
	 * Throws an exception if conditions for restoring are not met.
	 */
	suspend fun checkCanRestore() {
		val isPatched = preferencesRepository.getIsPatched()
		if (!isPatched) {
			throw LocalizedException(LocalizedString.resource(R.string.error_not_patched))
		}
		if (!isBackupAvailable()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_backup_not_found))
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throw LocalizedException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}

	private fun isBackupAvailable() = with(strategy) {
		apk.backupExists && obb.backupExists
	}
}