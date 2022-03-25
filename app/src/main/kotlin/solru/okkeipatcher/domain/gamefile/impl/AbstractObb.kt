package solru.okkeipatcher.domain.gamefile.impl

import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.base.ObservableImpl
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.PatchableGameFile
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.util.extension.reset
import solru.okkeipatcher.util.Preferences

abstract class AbstractObb(protected val commonFiles: CommonFiles) : ObservableImpl(), PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFiles.backupObb.exists

	override val progress = merge(
		commonFiles.backupObb.progress,
		commonFiles.obbToBackup.progress,
		progressPublisher._progress
	)

	override fun canPatch(onNegative: (LocalizedString) -> Unit): Boolean {
		if (!commonFiles.obbToPatch.exists && !backupExists) {
			onNegative(LocalizedString.resource(R.string.error_obb_not_found))
			return false
		}
		return true
	}

	override fun deleteBackup() = commonFiles.backupObb.delete()

	override suspend fun backup() {
		progressPublisher._progress.reset()
		_status.emit(LocalizedString.resource(R.string.status_comparing_obb))
		if (verifyBackupIntegrity()) return
		try {
			if (!commonFiles.obbToBackup.exists) {
				throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
			}
			_status.emit(LocalizedString.resource(R.string.status_backing_up_obb))
			val hash = commonFiles.obbToBackup.copyTo(commonFiles.backupObb, hashing = true)
			_status.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			Preferences.set(CommonFileHashKey.backup_obb_hash.name, hash)
		} catch (t: Throwable) {
			commonFiles.backupObb.delete()
			throw t
		}
	}

	override suspend fun restore() {
		try {
			progressPublisher._progress.reset()
			if (!commonFiles.backupObb.exists) {
				throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
			}
			_status.emit(LocalizedString.resource(R.string.status_restoring_obb))
			commonFiles.backupObb.copyTo(commonFiles.obbToBackup)
		} catch (t: Throwable) {
			commonFiles.obbToBackup.delete()
			throw t
		}
	}

	override suspend fun verifyBackupIntegrity() = commonFiles.backupObb.verify()
}