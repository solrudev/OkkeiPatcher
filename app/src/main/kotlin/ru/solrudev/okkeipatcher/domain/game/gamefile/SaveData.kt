package ru.solrudev.okkeipatcher.domain.game.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import javax.inject.Inject

class SaveData @Inject constructor(private val saveDataRepository: SaveDataRepository) : GameFile {

	override val backupExists: Boolean
		get() = saveDataRepository.backupExists

	override fun deleteBackup() = saveDataRepository.deleteBackup()

	override fun backup(): Operation<Unit> = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_backing_up_save_data))
		saveDataRepository.createTemp().onFailure { failure ->
			message(createWarning(failure.reason))
		}
	}

	override fun restore() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_comparing_saves))
		if (saveDataRepository.verifyBackup()) {
			status(LocalizedString.resource(R.string.status_restoring_saves))
			saveDataRepository.restoreBackup().onFailure { failure ->
				message(createWarning(failure.reason))
			}
		} else {
			saveDataRepository.deleteBackup()
			val warningMessage = LocalizedString.resource(R.string.warning_save_data_backup_not_found_or_corrupted)
			message(createWarning(warningMessage))
		}
		status(LocalizedString.resource(R.string.status_persisting_save_data))
		saveDataRepository.persistTempAsBackup()
	}

	override fun close() = saveDataRepository.deleteTemp()

	private fun createWarning(message: LocalizedString) = Message(
		LocalizedString.resource(R.string.warning),
		message
	)
}