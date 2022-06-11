package ru.solrudev.okkeipatcher.domain.service.gamefile

import androidx.annotation.StringRes
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.file.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.file.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.util.Preferences
import javax.inject.Inject

class SaveData @Inject constructor(private val commonFiles: CommonFiles) : GameFile {

	override val backupExists: Boolean
		get() = commonFiles.backupSaveData.exists

	override fun deleteBackup() = commonFiles.backupSaveData.delete()

	override fun backup() = object : AbstractOperation<Unit>() {

		private val backupSaveDataOperation = commonFiles.originalSaveData.copyTo(commonFiles.tempSaveData)
		override val progressDelta = withProgressDeltaFlows(backupSaveDataOperation.progressDelta)
		override val progressMax = 100

		override suspend fun invoke() {
			if (commonFiles.originalSaveData.exists) {
				if (commonFiles.originalSaveData.verify().invoke()) {
					progressDelta(progressMax)
					return
				}
				status(LocalizedString.resource(R.string.status_backing_up_save_data))
				backupSaveDataOperation()
				return
			}
			message(createWarning(R.string.warning_save_data_not_found))
			progressDelta(progressMax)
		}
	}

	override fun restore() = object : AbstractOperation<Unit>() {

		private val restoreSaveDataOperation = commonFiles.backupSaveData.copyTo(commonFiles.originalSaveData)
		override val progressDelta = withProgressDeltaFlows(restoreSaveDataOperation.progressDelta)
		override val progressMax = restoreSaveDataOperation.progressMax

		override suspend fun invoke() {
			status(LocalizedString.resource(R.string.status_comparing_saves))
			if (commonFiles.backupSaveData.verify().invoke()) {
				status(LocalizedString.resource(R.string.status_restoring_saves))
				restoreSaveDataOperation()
			} else {
				commonFiles.backupSaveData.delete()
				message(createWarning(R.string.warning_save_data_backup_not_found_or_corrupted))
				progressDelta(progressMax)
			}
			if (commonFiles.tempSaveData.exists) {
				commonFiles.backupSaveData.delete()
				commonFiles.tempSaveData.renameTo(commonFiles.backupSaveData.name)
			}
			if (!commonFiles.backupSaveData.exists) {
				return
			}
			status(LocalizedString.resource(R.string.status_writing_save_data_hash))
			Preferences.set(
				CommonFileHashKey.save_data_hash.name,
				commonFiles.backupSaveData.computeHash().invoke()
			)
			progressDelta(progressMax)
		}
	}

	override fun close() = commonFiles.tempSaveData.delete()

	private fun createWarning(@StringRes message: Int) = Message(
		LocalizedString.resource(R.string.warning),
		LocalizedString.resource(message)
	)
}