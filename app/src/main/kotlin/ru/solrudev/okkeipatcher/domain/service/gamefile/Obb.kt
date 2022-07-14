package ru.solrudev.okkeipatcher.domain.service.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.toOperation
import ru.solrudev.okkeipatcher.domain.model.exception.ObbCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository

abstract class Obb(private val obbRepository: ObbRepository) : PatchableGameFile {

	override val backupExists: Boolean
		get() = obbRepository.backupExists

	override fun canPatch(): Result {
		if (!obbRepository.obbExists && !backupExists) {
			return Result.Failure(LocalizedString.resource(R.string.error_obb_not_found))
		}
		return Result.Success
	}

	override fun deleteBackup() = obbRepository.deleteBackup()

	override fun backup(): Operation<Unit> {
		val verifyBackupOperation = obbRepository.verifyBackup().toOperation()
		val backupOperation = obbRepository.backup().toOperation()
		return operation(verifyBackupOperation, backupOperation) {
			status(LocalizedString.resource(R.string.status_comparing_obb))
			if (!verifyBackupOperation()) {
				status(LocalizedString.resource(R.string.status_backing_up_obb))
				backupOperation()
			}
		}
	}

	override fun restore(): Operation<Unit> {
		val verifyBackupOperation = obbRepository.verifyBackup().toOperation()
		val restoreOperation = obbRepository.restore().toOperation()
		return operation(verifyBackupOperation, restoreOperation) {
			status(LocalizedString.resource(R.string.status_restoring_obb))
			if (!verifyBackupOperation()) {
				throw ObbCorruptedException()
			}
			restoreOperation()
		}
	}
}