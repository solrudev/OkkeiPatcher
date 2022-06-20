package ru.solrudev.okkeipatcher.domain.service.gamefile

import androidx.annotation.StringRes
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
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

	override fun backup(): Operation<Unit> {
		val backupSaveDataOperation = commonFiles.originalSaveData.copyTo(commonFiles.tempSaveData)
		return operation(backupSaveDataOperation) {
			if (commonFiles.originalSaveData.exists) {
				if (commonFiles.originalSaveData.verify().invoke()) {
					return@operation
				}
				status(LocalizedString.resource(R.string.status_backing_up_save_data))
				backupSaveDataOperation()
				return@operation
			}
			message(createWarning(R.string.warning_save_data_not_found))
		}
	}

	override fun restore(): Operation<Unit> {
		val restoreSaveDataOperation = commonFiles.backupSaveData.copyTo(commonFiles.originalSaveData)
		return operation(restoreSaveDataOperation) {
			status(LocalizedString.resource(R.string.status_comparing_saves))
			if (commonFiles.backupSaveData.verify().invoke()) {
				status(LocalizedString.resource(R.string.status_restoring_saves))
				restoreSaveDataOperation()
			} else {
				commonFiles.backupSaveData.delete()
				message(createWarning(R.string.warning_save_data_backup_not_found_or_corrupted))
			}
			if (commonFiles.tempSaveData.exists) {
				commonFiles.backupSaveData.delete()
				commonFiles.tempSaveData.renameTo(commonFiles.backupSaveData.name)
			}
			if (!commonFiles.backupSaveData.exists) {
				return@operation
			}
			status(LocalizedString.resource(R.string.status_writing_save_data_hash))
			Preferences.set(
				CommonFileHashKey.save_data_hash.name,
				commonFiles.backupSaveData.computeHash().invoke()
			)
		}
	}

	override fun close() = commonFiles.tempSaveData.delete()

	private fun createWarning(@StringRes message: Int) = Message(
		LocalizedString.resource(R.string.warning),
		LocalizedString.resource(message)
	)
}