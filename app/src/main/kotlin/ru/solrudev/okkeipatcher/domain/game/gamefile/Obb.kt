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
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.asOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.exception.IncompatibleObbException
import ru.solrudev.okkeipatcher.domain.model.exception.ObbCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.isCompatible

abstract class Obb(
	private val obbPatchFiles: PatchFiles,
	private val obbPatchOperation: Operation<Unit>,
	private val obbRepository: ObbRepository,
	private val obbBackupRepository: ObbBackupRepository
) : PatchableGameFile {

	override val backupExists: Boolean
		get() = obbBackupRepository.backupExists

	override fun canPatch(): Result<Unit> {
		if (!obbRepository.obbExists && !backupExists) {
			return Result.failure(R.string.error_obb_not_found)
		}
		return Result.success()
	}

	override fun patch() = obbPatchOperation
	override fun update() = patch()
	override fun deleteBackup() = obbBackupRepository.deleteBackup()

	override fun backup(): Operation<Unit> {
		val verifyBackupOperation = obbBackupRepository.verifyBackup().asOperation()
		val backupOperation = obbBackupRepository.createBackup().asOperation()
		return operation(verifyBackupOperation, backupOperation) {
			status(R.string.status_comparing_obb)
			if (!verifyBackupOperation()) {
				status(R.string.status_backing_up_obb)
				val obbHash = backupOperation()
				if (!obbPatchFiles.isCompatible(obbHash)) {
					obbBackupRepository.deleteBackup()
					throw IncompatibleObbException()
				}
			}
		}
	}

	override fun restore(): Operation<Unit> {
		val verifyBackupOperation = obbBackupRepository.verifyBackup().asOperation()
		val restoreOperation = obbBackupRepository.restoreBackup().asOperation()
		return operation(verifyBackupOperation, restoreOperation) {
			status(R.string.status_restoring_obb)
			if (!verifyBackupOperation()) {
				throw ObbCorruptedException()
			}
			restoreOperation()
		}
	}
}