package ru.solrudev.okkeipatcher.domain.game.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.asOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.exception.ObbCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository

abstract class Obb(
	private val obbRepository: ObbRepository,
	private val obbBackupRepository: ObbBackupRepository
) : PatchableGameFile {

	override val backupExists: Boolean
		get() = obbBackupRepository.backupExists

	override fun canPatch(): Result {
		if (!obbRepository.obbExists && !backupExists) {
			return Result.failure(R.string.error_obb_not_found)
		}
		return Result.success()
	}

	override fun deleteBackup() = obbBackupRepository.deleteBackup()

	override fun backup(): Operation<Unit> {
		val verifyBackupOperation = obbBackupRepository.verifyBackup().asOperation()
		val backupOperation = obbBackupRepository.createBackup().asOperation()
		return operation(verifyBackupOperation, backupOperation) {
			status(R.string.status_comparing_obb)
			if (!verifyBackupOperation()) {
				status(R.string.status_backing_up_obb)
				backupOperation()
			}
		}
	}

	override fun restore(): Operation<Unit> {
		val verifyBackupOperation = obbBackupRepository.verifyBackup().asOperation()
		val restoreOperation = obbBackupRepository.restoreBackup().asOperation()
		return operation(verifyBackupOperation, restoreOperation) {
			status(R.string.status_restoring_obb)
			if (!verifyBackupOperation()) {
				throw ObbCorruptedException()
			}
			restoreOperation()
		}
	}
}