package solru.okkeipatcher.domain.gamefile.impl

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.SaveData
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message
import solru.okkeipatcher.domain.operation.AbstractOperation
import solru.okkeipatcher.util.Preferences
import javax.inject.Inject

class SaveDataImpl @Inject constructor(private val commonFiles: CommonFiles) : SaveData {

	override val backupExists: Boolean
		get() = commonFiles.backupSaveData.exists

	override fun deleteBackup() = commonFiles.backupSaveData.delete()

	override fun backup() = object : AbstractOperation<Unit>() {

		private val backupSaveDataOperation = commonFiles.originalSaveData.copyTo(commonFiles.tempSaveData)
		override val progressDelta = merge(backupSaveDataOperation.progressDelta, _progressDelta)
		override val progressMax = 100

		override suspend fun invoke() {
			if (commonFiles.originalSaveData.exists) {
				if (commonFiles.originalSaveData.verify().invoke()) {
					_progressDelta.emit(progressMax)
					return
				}
				_status.emit(LocalizedString.resource(R.string.status_backing_up_save_data))
				backupSaveDataOperation()
				return
			}
			_messages.sendWarning(R.string.warning_save_data_not_found)
			_progressDelta.emit(progressMax)
		}
	}

	override fun restore() = object : AbstractOperation<Unit>() {

		private val restoreSaveDataOperation = commonFiles.backupSaveData.copyTo(commonFiles.originalSaveData)
		override val progressDelta = merge(restoreSaveDataOperation.progressDelta, _progressDelta)
		override val progressMax = restoreSaveDataOperation.progressMax

		override suspend fun invoke() {
			_status.emit(LocalizedString.resource(R.string.status_comparing_saves))
			if (commonFiles.backupSaveData.verify().invoke()) {
				_status.emit(LocalizedString.resource(R.string.status_restoring_saves))
				restoreSaveDataOperation()
			} else {
				commonFiles.backupSaveData.delete()
				_messages.sendWarning(R.string.warning_save_data_backup_not_found_or_corrupted)
				_progressDelta.emit(progressMax)
			}
			if (commonFiles.tempSaveData.exists) {
				commonFiles.backupSaveData.delete()
				commonFiles.tempSaveData.renameTo(commonFiles.backupSaveData.name)
			}
			if (!commonFiles.backupSaveData.exists) {
				return
			}
			_status.emit(LocalizedString.resource(R.string.status_writing_save_data_hash))
			Preferences.set(
				CommonFileHashKey.save_data_hash.name,
				commonFiles.backupSaveData.computeHash().invoke()
			)
			_progressDelta.emit(progressMax)
		}
	}

	override fun close() = commonFiles.tempSaveData.delete()

	private suspend inline fun MutableSharedFlow<Message>.sendWarning(message: Int) {
		val warningMessage = Message(
			LocalizedString.resource(R.string.warning),
			LocalizedString.resource(message)
		)
		emit(warningMessage)
	}
}