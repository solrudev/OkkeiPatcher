package ru.solrudev.okkeipatcher.domain.service.operation

import android.content.Context
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.emptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.isEnoughSpace
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.RestoreStrategy

class RestoreOperation(
	private val strategy: RestoreStrategy,
	private val handleSaveData: Boolean,
	private val isPatched: Dao<Boolean>,
	private val applicationContext: Context
) : Operation<Unit> {

	private val operation = with(strategy) {
		aggregateOperation(
			if (handleSaveData) saveData.backup() else emptyOperation(),
			apk.restore(),
			obb.restore(),
			if (handleSaveData) saveData.restore() else emptyOperation(),
			operation {
				apk.deleteBackup()
				obb.deleteBackup()
				isPatched.persist(false)
			}
		)
	}

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun invoke() = strategy.use {
		operation()
	}

	/**
	 * Throws an exception if conditions for restoring are not met.
	 */
	suspend fun checkCanRestore() {
		val isPatched = isPatched.retrieve()
		if (!isPatched) {
			throw LocalizedException(LocalizedString.resource(R.string.error_not_patched))
		}
		if (!isBackupAvailable()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_backup_not_found))
		}
		if (!applicationContext.isEnoughSpace) {
			throw LocalizedException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}

	private fun isBackupAvailable() = with(strategy) {
		apk.backupExists && obb.backupExists
	}
}