package solru.okkeipatcher.core.services.files.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.core.services.ObservableServiceImpl
import solru.okkeipatcher.core.services.files.SaveData
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.Message
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFiles
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class SaveDataGeneric @Inject constructor(private val commonFiles: CommonFiles) : ObservableServiceImpl(), SaveData {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		commonFiles.backupSaveData.progress,
		commonFiles.originalSaveData.progress,
		commonFiles.tempSaveData.progress,
		progressProvider.mutableProgress
	)

	override val backupExists: Boolean
		get() = commonFiles.backupSaveData.exists

	override fun deleteBackup() = commonFiles.backupSaveData.delete()

	override suspend fun backup() {
		progressProvider.mutableProgress.reset()
		if (commonFiles.originalSaveData.exists) {
			if (commonFiles.originalSaveData.verify()) return
			mutableStatus.emit(LocalizedString.resource(R.string.status_backing_up_save_data))
			commonFiles.originalSaveData.copyTo(commonFiles.tempSaveData)
			return
		}
		sendWarningMessage(R.string.warning_save_data_not_found)
	}

	override suspend fun restore() {
		progressProvider.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_saves))
		if (verifyBackupIntegrity()) {
			mutableStatus.emit(LocalizedString.resource(R.string.status_restoring_saves))
			commonFiles.backupSaveData.copyTo(commonFiles.originalSaveData)
		} else {
			commonFiles.backupSaveData.delete()
			sendWarningMessage(R.string.warning_save_data_backup_not_found_or_corrupted)
		}
		if (commonFiles.tempSaveData.exists) {
			commonFiles.backupSaveData.delete()
			commonFiles.tempSaveData.renameTo(commonFiles.backupSaveData.name)
		}
		if (!commonFiles.backupSaveData.exists) return
		mutableStatus.emit(LocalizedString.resource(R.string.status_writing_save_data_hash))
		Preferences.set(
			CommonFileHashKey.save_data_hash.name,
			commonFiles.backupSaveData.computeHash()
		)
	}

	override suspend fun verifyBackupIntegrity() = commonFiles.backupSaveData.verify()
	override fun close() = commonFiles.tempSaveData.delete()

	private suspend inline fun sendWarningMessage(message: Int) {
		val warningMessage = Message(
			LocalizedString.resource(R.string.warning),
			LocalizedString.resource(message)
		)
		mutableMessages.emit(warningMessage)
	}
}