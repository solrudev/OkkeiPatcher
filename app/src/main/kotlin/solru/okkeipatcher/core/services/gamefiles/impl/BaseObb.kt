package solru.okkeipatcher.core.services.gamefiles.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.core.model.files.common.CommonFileHashKey
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.services.ObservableServiceImpl
import solru.okkeipatcher.core.services.gamefiles.PatchableGameFile
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset

abstract class BaseObb(protected val commonFiles: CommonFiles) : ObservableServiceImpl(), PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFiles.backupObb.exists

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFiles.backupObb.progress,
		commonFiles.obbToBackup.progress,
		commonFiles.obbToPatch.progress,
		progressPublisher.mutableProgress
	)

	override fun deleteBackup() = commonFiles.backupObb.delete()

	override suspend fun backup() {
		progressPublisher.mutableProgress.reset()
		if (!commonFiles.obbToBackup.exists) {
			throw OkkeiException(LocalizedString.resource(R.string.error_obb_not_found))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_obb))
		if (verifyBackupIntegrity()) return
		try {
			progressPublisher.mutableProgress.reset()
			if (!commonFiles.obbToBackup.exists) {
				throw OkkeiException(LocalizedString.resource(R.string.error_obb_not_found))
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_obb))
			if (verifyBackupIntegrity()) return
			mutableStatus.emit(LocalizedString.resource(R.string.status_backing_up_obb))
			val hash = commonFiles.obbToBackup.copyTo(commonFiles.backupObb, hashing = true)
			mutableStatus.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			Preferences.set(CommonFileHashKey.backup_obb_hash.name, hash)
		} catch (e: Throwable) {
			commonFiles.backupObb.delete()
			throw e
		}
	}

	override suspend fun restore() {
		try {
			progressPublisher.mutableProgress.reset()
			if (!commonFiles.backupObb.exists) {
				throw OkkeiException(LocalizedString.resource(R.string.error_obb_not_found))
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_restoring_obb))
			commonFiles.backupObb.copyTo(commonFiles.obbToBackup)
		} catch (e: Throwable) {
			commonFiles.obbToBackup.delete()
			throw e
		}
	}

	override suspend fun verifyBackupIntegrity() = commonFiles.backupObb.verify()
}