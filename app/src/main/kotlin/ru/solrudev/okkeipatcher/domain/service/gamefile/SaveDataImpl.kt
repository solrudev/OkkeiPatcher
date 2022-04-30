package ru.solrudev.okkeipatcher.domain.service.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.file.common.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.util.Preferences
import javax.inject.Inject

class SaveDataImpl @Inject constructor(private val commonFiles: CommonFiles) : SaveData {

	override val backupExists: Boolean
		get() = commonFiles.backupSaveData.exists

	override fun deleteBackup() = commonFiles.backupSaveData.delete()

	override fun backup() = object : AbstractOperation<Unit>() {

		private val backupSaveDataOperation = commonFiles.originalSaveData.copyTo(commonFiles.tempSaveData)
		override val progressDelta = addProgressDeltaFlows(backupSaveDataOperation.progressDelta)
		override val progressMax = 100

		override suspend fun invoke() {
			if (commonFiles.originalSaveData.exists) {
				if (commonFiles.originalSaveData.verify().invoke()) {
					emitProgressDelta(progressMax)
					return
				}
				emitStatus(LocalizedString.resource(R.string.status_backing_up_save_data))
				backupSaveDataOperation()
				return
			}
			emitMessage(createWarning(R.string.warning_save_data_not_found))
			emitProgressDelta(progressMax)
		}
	}

	override fun restore() = object : AbstractOperation<Unit>() {

		private val restoreSaveDataOperation = commonFiles.backupSaveData.copyTo(commonFiles.originalSaveData)
		override val progressDelta = addProgressDeltaFlows(restoreSaveDataOperation.progressDelta)
		override val progressMax = restoreSaveDataOperation.progressMax

		override suspend fun invoke() {
			emitStatus(LocalizedString.resource(R.string.status_comparing_saves))
			if (commonFiles.backupSaveData.verify().invoke()) {
				emitStatus(LocalizedString.resource(R.string.status_restoring_saves))
				restoreSaveDataOperation()
			} else {
				commonFiles.backupSaveData.delete()
				emitMessage(createWarning(R.string.warning_save_data_backup_not_found_or_corrupted))
				emitProgressDelta(progressMax)
			}
			if (commonFiles.tempSaveData.exists) {
				commonFiles.backupSaveData.delete()
				commonFiles.tempSaveData.renameTo(commonFiles.backupSaveData.name)
			}
			if (!commonFiles.backupSaveData.exists) {
				return
			}
			emitStatus(LocalizedString.resource(R.string.status_writing_save_data_hash))
			Preferences.set(
				CommonFileHashKey.save_data_hash.name,
				commonFiles.backupSaveData.computeHash().invoke()
			)
			emitProgressDelta(progressMax)
		}
	}

	override fun close() = commonFiles.tempSaveData.delete()

	private fun createWarning(message: Int) = Message(
		LocalizedString.resource(R.string.warning),
		LocalizedString.resource(message)
	)
}