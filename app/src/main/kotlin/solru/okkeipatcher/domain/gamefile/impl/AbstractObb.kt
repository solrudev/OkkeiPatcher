package solru.okkeipatcher.domain.gamefile.impl

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.PatchableGameFile
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.operation.AbstractOperation
import solru.okkeipatcher.util.Preferences

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

		override val progressDelta = merge(
			verifyBackupObbOperation.progressDelta,
			backupObbOperation.progressDelta.map { it * 6 },
			_progressDelta
		)

		override val progressMax = verifyBackupObbOperation.progressMax + backupObbOperation.progressMax * 6

		override suspend fun invoke() {
			_status.emit(LocalizedString.resource(R.string.status_comparing_obb))
			if (verifyBackupObbOperation()) {
				_progressDelta.emit(progressMax - verifyBackupObbOperation.progressMax)
				return
			}
			try {
				if (!commonFiles.obbToBackup.exists) {
					throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
				}
				_status.emit(LocalizedString.resource(R.string.status_backing_up_obb))
				val hash = backupObbOperation()
				_status.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
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
				_status.emit(LocalizedString.resource(R.string.status_restoring_obb))
				restoreObbOperation()
			} catch (t: Throwable) {
				commonFiles.obbToBackup.delete()
				throw t
			}
		}
	}
}