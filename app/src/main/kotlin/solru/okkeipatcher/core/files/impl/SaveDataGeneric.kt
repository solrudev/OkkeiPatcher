package solru.okkeipatcher.core.files.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.core.base.ProgressProviderImpl
import solru.okkeipatcher.core.files.base.SaveData
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFileInstances
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class SaveDataGeneric @Inject constructor(private val commonFileInstances: CommonFileInstances) :
	AppServiceBase(ProgressProviderImpl()), SaveData {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFileInstances.backupSaveData.progress,
		commonFileInstances.originalSaveData.progress,
		commonFileInstances.tempSaveData.progress,
		progressProvider.mutableProgress
	)

	override val backupExists: Boolean
		get() = commonFileInstances.backupSaveData.exists

	override fun deleteBackup() {
		commonFileInstances.backupSaveData.delete()
	}

	override suspend fun backup() = tryWrapper(onCatch = { clearTempFiles() }) {
		progressProvider.mutableProgress.reset()
		if (commonFileInstances.originalSaveData.exists) {
			if (commonFileInstances.originalSaveData.verify()) return
			statusMutable.emit(LocalizedString.resource(R.string.status_backing_up_save_data))
			commonFileInstances.originalSaveData.copyTo(commonFileInstances.tempSaveData)
			return
		}
		sendWarningMessage(R.string.warning_save_data_not_found)
	}

	override suspend fun restore() = tryWrapper {
		progressProvider.mutableProgress.reset()
		statusMutable.emit(LocalizedString.resource(R.string.status_comparing_saves))
		if (verifyBackupIntegrity()) {
			statusMutable.emit(LocalizedString.resource(R.string.status_restoring_saves))
			commonFileInstances.backupSaveData.copyTo(commonFileInstances.originalSaveData)
		} else {
			commonFileInstances.backupSaveData.delete()
			sendWarningMessage(R.string.warning_save_data_backup_not_found_or_corrupted)
		}
		if (commonFileInstances.tempSaveData.exists) {
			commonFileInstances.backupSaveData.delete()
			commonFileInstances.tempSaveData.renameTo(commonFileInstances.backupSaveData.name)
		}
		if (!commonFileInstances.backupSaveData.exists) return
		statusMutable.emit(LocalizedString.resource(R.string.status_writing_save_data_hash))
		Preferences.set(
			CommonFileHashKey.save_data_hash.name,
			commonFileInstances.backupSaveData.computeHash()
		)
	}

	override suspend fun verifyBackupIntegrity() = commonFileInstances.backupSaveData.verify()

	override fun clearTempFiles() = commonFileInstances.tempSaveData.delete()
}