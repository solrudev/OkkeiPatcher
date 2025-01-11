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
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.exception.ApkNotFoundException
import ru.solrudev.okkeipatcher.domain.model.exception.IncompatibleApkException
import ru.solrudev.okkeipatcher.domain.model.exception.InstallException
import ru.solrudev.okkeipatcher.domain.model.exception.NotTrustworthyApkException
import ru.solrudev.okkeipatcher.domain.model.exception.UninstallException
import ru.solrudev.okkeipatcher.domain.operation.factory.ApkPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.isCompatible
import ru.solrudev.okkeipatcher.domain.repository.patch.updateInstalledVersion

abstract class Apk(
	private val apkPatchFiles: PatchFiles,
	private val apkPatchOperationFactory: ApkPatchOperationFactory,
	private val apkRepository: ApkRepository,
	private val apkBackupRepository: ApkBackupRepository
) : PatchableGameFile {

	override val backupExists: Boolean
		get() = apkBackupRepository.backupExists

	override fun canPatch(): Result<Unit> {
		val canInstallPatchedApk = apkBackupRepository.backupExists && apkRepository.tempExists
		if (!apkRepository.isInstalled && !canInstallPatchedApk) {
			return Result.failure(R.string.error_game_not_found)
		}
		return Result.success()
	}

	override fun patch() = patch(updating = false)
	override fun update() = patch(updating = true)

	override fun deleteBackup() = apkBackupRepository.deleteBackup()

	override fun backup() = operation(progressMax = 50) {
		status(R.string.status_comparing_apk)
		if (apkBackupRepository.verifyBackup()) {
			if (apkRepository.verifyTemp()) {
				return@operation
			}
			apkRepository.deleteTemp()
			val apkHash = apkRepository.getTemp().use { it.computeHash() }
			if (!apkPatchFiles.isCompatible(apkHash)) {
				apkRepository.deleteTemp()
				throw IncompatibleApkException()
			}
		} else {
			status(R.string.status_backing_up_apk)
			val apkHash = apkBackupRepository.createBackup()
			if (!apkPatchFiles.isCompatible(apkHash)) {
				apkBackupRepository.deleteBackup()
				throw IncompatibleApkException()
			}
		}
	}

	override fun restore(): Operation<Unit> {
		val uninstallOperation = uninstall(updating = false)
		val installBackupOperation = install(apkBackupRepository::installBackup)
		return operation(uninstallOperation, installBackupOperation) {
			if (!apkBackupRepository.backupExists) {
				throw ApkNotFoundException()
			}
			status(R.string.status_comparing_apk)
			if (!apkBackupRepository.verifyBackup()) {
				throw NotTrustworthyApkException()
			}
			uninstallOperation()
			installBackupOperation()
		}
	}

	private fun patch(updating: Boolean): Operation<Unit> {
		val installPatchedOperation = installPatched(updating)
		val apkPatchOperation = apkPatchOperationFactory.create(apkPatchFiles)
		return operation(apkPatchOperation, installPatchedOperation) {
			status(R.string.status_comparing_apk)
			if (apkRepository.verifyTemp()) {
				apkPatchOperation.skip()
				installPatchedOperation()
				apkPatchFiles.updateInstalledVersion()
				return@operation
			}
			apkRepository.deleteTemp()
			apkPatchOperation()
			installPatchedOperation()
			apkPatchFiles.updateInstalledVersion()
		}
	}

	private fun installPatched(updating: Boolean): Operation<Unit> {
		val uninstallOperation = uninstall(updating)
		val installOperation = install(apkRepository::installTemp)
		return operation(uninstallOperation, installOperation) {
			if (!apkRepository.tempExists) {
				throw ApkNotFoundException()
			}
			status(R.string.status_comparing_apk)
			if (!apkRepository.verifyTemp()) {
				apkRepository.deleteTemp()
				throw NotTrustworthyApkException()
			}
			uninstallOperation()
			installOperation()
			apkRepository.deleteTemp()
		}
	}

	private fun uninstall(updating: Boolean) = operation(progressMax = 100) {
		status(R.string.status_uninstalling)
		if (updating || !apkRepository.isInstalled) {
			return@operation
		}
		apkRepository.uninstall().onFailure { failure -> throw UninstallException(failure.reason) }
	}

	private inline fun install(crossinline installApk: suspend () -> Result<Unit>): Operation<Unit> =
		operation(progressMax = 100) {
			status(R.string.status_installing)
			installApk().onFailure { failure -> throw InstallException(failure.reason) }
		}
}