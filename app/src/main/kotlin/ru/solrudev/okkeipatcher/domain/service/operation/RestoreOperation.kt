package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.emptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.model.RestoreParameters
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.RestoreStrategy

class RestoreOperation(
	private val parameters: RestoreParameters,
	private val strategy: RestoreStrategy,
	private val patchVersionPersistable: Persistable<String>,
	private val patchStatus: Dao<Boolean>,
	private val storageChecker: StorageChecker
) : Operation<Result> {

	private val operation = with(strategy) {
		aggregateOperation(
			if (parameters.handleSaveData) saveData.backup() else emptyOperation(),
			apk.restore(),
			if (parameters.handleSaveData) saveData.restore() else emptyOperation(),
			obb.restore(),
			operation {
				patchVersionPersistable.clear()
				apk.deleteBackup()
				obb.deleteBackup()
				patchStatus.persist(false)
			}
		)
	}

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun canInvoke(): Result {
		val isPatched = patchStatus.retrieve()
		if (!isPatched) {
			return Result.Failure(LocalizedString.resource(R.string.error_not_patched))
		}
		if (!isBackupAvailable()) {
			return Result.Failure(LocalizedString.resource(R.string.error_backup_not_found))
		}
		if (!storageChecker.isEnoughSpace()) {
			return Result.Failure(LocalizedString.resource(R.string.error_no_free_space))
		}
		return Result.Success
	}

	override suspend fun invoke() = wrapDomainExceptions {
		strategy.use {
			operation()
		}
	}

	private fun isBackupAvailable() = with(strategy) {
		apk.backupExists && obb.backupExists
	}
}