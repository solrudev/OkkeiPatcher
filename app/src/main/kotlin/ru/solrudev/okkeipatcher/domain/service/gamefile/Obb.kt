package ru.solrudev.okkeipatcher.domain.service.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.file.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.file.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.util.Preferences

abstract class Obb(protected val commonFiles: CommonFiles) : PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFiles.backupObb.exists

	override fun canPatch(onNegative: (LocalizedString) -> Unit): Boolean {
		if (!commonFiles.obbToPatch.exists && !backupExists) {
			onNegative(LocalizedString.resource(R.string.error_obb_not_found))
			return false
		}
		return true
	}

	override fun deleteBackup() = commonFiles.backupObb.delete()

	override fun backup(): Operation<Unit> {
		val verifyBackupObbOperation = commonFiles.backupObb.verify()
		val backupObbOperation = commonFiles.obbToBackup.copyTo(commonFiles.backupObb, hashing = true)
		return operation(verifyBackupObbOperation, backupObbOperation) {
			status(LocalizedString.resource(R.string.status_comparing_obb))
			if (verifyBackupObbOperation()) {
				return@operation
			}
			try {
				if (!commonFiles.obbToBackup.exists) {
					throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
				}
				status(LocalizedString.resource(R.string.status_backing_up_obb))
				val hash = backupObbOperation()
				status(LocalizedString.resource(R.string.status_writing_obb_hash))
				Preferences.set(CommonFileHashKey.backup_obb_hash.name, hash)
			} catch (t: Throwable) {
				commonFiles.backupObb.delete()
				throw t
			}
		}
	}

	override fun restore(): Operation<Unit> {
		val restoreObbOperation = commonFiles.backupObb.copyTo(commonFiles.obbToBackup)
		return operation(restoreObbOperation) {
			try {
				if (!commonFiles.backupObb.exists) {
					throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
				}
				status(LocalizedString.resource(R.string.status_restoring_obb))
				restoreObbOperation()
			} catch (t: Throwable) {
				commonFiles.obbToBackup.delete()
				throw t
			}
		}
	}

	override fun close() {}
}