package ru.solrudev.okkeipatcher.domain.service.gamefile

import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.file.common.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.util.Preferences

abstract class AbstractObb(protected val commonFiles: CommonFiles) : PatchableGameFile {

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

	override fun backup() = object : AbstractOperation<Unit>() {

		private val verifyBackupObbOperation = commonFiles.backupObb.verify()
		private val backupObbOperation = commonFiles.obbToBackup.copyTo(commonFiles.backupObb, hashing = true)

		override val progressDelta = withProgressDeltaFlows(
			verifyBackupObbOperation.progressDelta,
			backupObbOperation.progressDelta.map { it * 6 }
		)

		override val progressMax = verifyBackupObbOperation.progressMax + backupObbOperation.progressMax * 6

		override suspend fun invoke() {
			status(LocalizedString.resource(R.string.status_comparing_obb))
			if (verifyBackupObbOperation()) {
				progressDelta(progressMax - verifyBackupObbOperation.progressMax)
				return
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

	override fun restore() = object : AbstractOperation<Unit>() {

		private val restoreObbOperation = commonFiles.backupObb.copyTo(commonFiles.obbToBackup)
		override val progressDelta = restoreObbOperation.progressDelta.map { it * 3 }
		override val progressMax = restoreObbOperation.progressMax * 3

		override suspend fun invoke() {
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
}