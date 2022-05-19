package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.domain.core.operation.AggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.EmptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.RestoreStrategy

class RestoreOperation(
	private val strategy: RestoreStrategy,
	private val handleSaveData: Boolean,
	private val isPatchedDao: Dao<Boolean>
) : Operation<Unit> {

	private val operation = AggregateOperation(
		operations = with(strategy) {
			listOf(
				if (handleSaveData) saveData.backup() else EmptyOperation,
				apk.restore(),
				obb.restore(),
				if (handleSaveData) saveData.restore() else EmptyOperation
			)
		},
		doAfter = {
			strategy.run {
				apk.deleteBackup()
				obb.deleteBackup()
				isPatchedDao.persist(false)
			}
		}
	)

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun invoke() = strategy.use { operation() }

	/**
	 * Throws an exception if conditions for restoring are not met.
	 */
	suspend fun checkCanRestore() {
		val isPatched = isPatchedDao.retrieve()
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