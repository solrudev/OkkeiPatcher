package solru.okkeipatcher.core.files.base

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFileInstances
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset

abstract class Obb(protected val commonFileInstances: CommonFileInstances) : AppServiceBase(),
	PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFileInstances.backupObb.exists

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFileInstances.backupObb.progress,
		commonFileInstances.obbToBackup.progress,
		commonFileInstances.obbToPatch.progress,
		progressMutable
	)

	override fun deleteBackup() {
		commonFileInstances.backupObb.deleteIfExists()
	}

	override suspend fun backup() {
		tryWrapper {
			progressMutable.reset()
			if (!commonFileInstances.obbToBackup.exists) {
				throwErrorMessage(R.string.error_obb_not_found)
			}
			statusMutable.emit(R.string.status_comparing_obb)
			if (verifyBackupIntegrity()) return
		}
		tryWrapper(onCatch = { commonFileInstances.backupObb.deleteIfExists() }) {
			progressMutable.reset()
			if (!commonFileInstances.obbToBackup.exists) {
				throwErrorMessage(R.string.error_obb_not_found)
			}
			statusMutable.emit(R.string.status_comparing_obb)
			if (verifyBackupIntegrity()) return
			statusMutable.emit(R.string.status_backing_up_obb)
			commonFileInstances.obbToBackup.copyTo(commonFileInstances.backupObb)
			statusMutable.emit(R.string.status_writing_obb_hash)
			Preferences.set(
				CommonFileHashKey.backup_obb_hash.name,
				commonFileInstances.backupObb.computeMd5()
			)
		}
	}

	override suspend fun restore() =
		tryWrapper(onCatch = { commonFileInstances.obbToBackup.deleteIfExists() }) {
			progressMutable.reset()
			if (!commonFileInstances.backupObb.exists) {
				throwErrorMessage(R.string.error_obb_not_found)
			}
			statusMutable.emit(R.string.status_restoring_obb)
			commonFileInstances.backupObb.copyTo(commonFileInstances.obbToBackup)
		}

	override suspend fun verifyBackupIntegrity() = commonFileInstances.backupObb.verify()
}