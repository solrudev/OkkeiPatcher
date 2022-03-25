package solru.okkeipatcher.domain.gamefile.impl

import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.base.ObservableImpl
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.SaveData
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message
import solru.okkeipatcher.domain.util.extension.reset
import solru.okkeipatcher.util.Preferences
import javax.inject.Inject

class SaveDataImpl @Inject constructor(private val commonFiles: CommonFiles) : ObservableImpl(), SaveData {

	override val progress = merge(
		commonFiles.backupSaveData.progress,
		commonFiles.originalSaveData.progress,
		commonFiles.tempSaveData.progress,
		progressPublisher._progress
	)

	override val backupExists: Boolean
		get() = commonFiles.backupSaveData.exists

	override fun deleteBackup() = commonFiles.backupSaveData.delete()

	override suspend fun backup() {
		progressPublisher._progress.reset()
		if (commonFiles.originalSaveData.exists) {
			if (commonFiles.originalSaveData.verify()) return
			_status.emit(LocalizedString.resource(R.string.status_backing_up_save_data))
			commonFiles.originalSaveData.copyTo(commonFiles.tempSaveData)
			return
		}
		sendWarningMessage(R.string.warning_save_data_not_found)
	}

	override suspend fun restore() {
		progressPublisher._progress.reset()
		_status.emit(LocalizedString.resource(R.string.status_comparing_saves))
		if (verifyBackupIntegrity()) {
			_status.emit(LocalizedString.resource(R.string.status_restoring_saves))
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
		_status.emit(LocalizedString.resource(R.string.status_writing_save_data_hash))
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
		_messages.emit(warningMessage)
	}
}