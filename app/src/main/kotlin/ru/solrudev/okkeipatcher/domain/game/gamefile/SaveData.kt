/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.domain.game.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.OperationScope
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import javax.inject.Inject

class SaveData @Inject constructor(private val saveDataRepository: SaveDataRepository) : GameFile {

	override val backupExists: Boolean
		get() = saveDataRepository.backupExists

	override fun deleteBackup() = saveDataRepository.deleteBackup()

	override fun backup(): Operation<Unit> = operation(progressMax = 50) {
		status(R.string.status_backing_up_save_data)
		saveDataRepository.createTemp().onFailure { failure ->
			warning(failure.reason)
		}
	}

	override fun restore() = operation(progressMax = 50) {
		status(R.string.status_comparing_saves)
		if (saveDataRepository.verifyBackup()) {
			status(R.string.status_restoring_saves)
			saveDataRepository.restoreBackup().onFailure { failure ->
				warning(failure.reason)
			}
		} else {
			saveDataRepository.deleteBackup()
			val warningMessage = LocalizedString.resource(R.string.warning_save_data_backup_not_found_or_corrupted)
			warning(warningMessage)
		}
		status(R.string.status_persisting_save_data)
		saveDataRepository.persistTempAsBackup()
	}

	override fun close() = saveDataRepository.deleteTemp()

	private suspend inline fun OperationScope.warning(message: LocalizedString) = message(
		Message(LocalizedString.resource(R.string.warning), message)
	)
}