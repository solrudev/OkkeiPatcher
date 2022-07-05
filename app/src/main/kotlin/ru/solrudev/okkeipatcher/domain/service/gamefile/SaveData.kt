package ru.solrudev.okkeipatcher.domain.service.gamefile

import androidx.annotation.StringRes
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import javax.inject.Inject

class SaveData @Inject constructor(private val saveDataRepository: SaveDataRepository) : GameFile {

	override val backupExists: Boolean
		get() = saveDataRepository.backupExists

	override fun deleteBackup() = saveDataRepository.deleteBackup()

	override fun backup() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_backing_up_save_data))
		val backupResult = saveDataRepository.createTemp()
		if (!backupResult) {
			message(createWarning(R.string.warning_could_not_backup_save_data))
		}
	}

	override fun restore() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_comparing_saves))
		if (saveDataRepository.verifyBackup()) {
			status(LocalizedString.resource(R.string.status_restoring_saves))
			saveDataRepository.restore()
		} else {
			saveDataRepository.deleteBackup()
			message(createWarning(R.string.warning_save_data_backup_not_found_or_corrupted))
		}
		status(LocalizedString.resource(R.string.status_persisting_save_data))
		saveDataRepository.persistTempAsBackup()
	}

	override fun close() = saveDataRepository.deleteTemp()

	private fun createWarning(@StringRes message: Int) = Message(
		LocalizedString.resource(R.string.warning),
		LocalizedString.resource(message)
	)
}