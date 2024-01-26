/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.domain.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.emptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.game.BackupableGame
import ru.solrudev.okkeipatcher.domain.model.RestoreParameters
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.service.StorageChecker

class RestoreOperation(
	private val parameters: RestoreParameters,
	private val game: BackupableGame,
	private val patchVersion: Persistable<String>,
	private val patchStatus: Dao<Boolean>,
	private val storageChecker: StorageChecker
) : Operation<Result<Unit>> {

	private val operation = game.restore()
	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun canInvoke(): Result<Unit> {
		val isPatched = patchStatus.retrieve()
		val failureReason = when {
			!isPatched -> R.string.error_not_patched
			!game.isBackupAvailable() -> R.string.error_backup_not_found
			!storageChecker.isEnoughSpace() -> R.string.error_no_free_space
			else -> null
		}
		if (failureReason != null) {
			return Result.failure(failureReason)
		}
		return Result.success()
	}

	override suspend fun invoke() = wrapDomainExceptions {
		game.use {
			operation()
		}
	}

	private fun BackupableGame.restore() = aggregateOperation(
		if (parameters.handleSaveData) saveData.backup() else emptyOperation(),
		apk.restore(),
		if (parameters.handleSaveData) saveData.restore() else emptyOperation(),
		obb.restore(),
		operation {
			patchVersion.clear()
			apk.deleteBackup()
			obb.deleteBackup()
			patchStatus.persist(false)
		}
	)

	private fun BackupableGame.isBackupAvailable() = apk.backupExists && obb.backupExists
}