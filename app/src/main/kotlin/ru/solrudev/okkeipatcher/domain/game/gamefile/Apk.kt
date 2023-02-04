package ru.solrudev.okkeipatcher.domain.game.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.exception.ApkNotFoundException
import ru.solrudev.okkeipatcher.domain.model.exception.InstallException
import ru.solrudev.okkeipatcher.domain.model.exception.NotTrustworthyApkException
import ru.solrudev.okkeipatcher.domain.model.exception.UninstallException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository

abstract class Apk(
	protected val apkRepository: ApkRepository,
	protected val apkBackupRepository: ApkBackupRepository
) : PatchableGameFile {

	override val backupExists: Boolean
		get() = apkBackupRepository.backupExists

	override fun canPatch(): Result {
		val canInstallPatchedApk = apkBackupRepository.backupExists && apkRepository.tempExists
		if (!apkRepository.isInstalled && !canInstallPatchedApk) {
			return Result.Failure(
				LocalizedString.resource(R.string.error_game_not_found)
			)
		}
		return Result.Success
	}

	override fun deleteBackup() = apkBackupRepository.deleteBackup()

	override fun backup() = operation(progressMax = 100) {
		status(R.string.status_comparing_apk)
		if (!apkBackupRepository.verifyBackup()) {
			status(R.string.status_backing_up_apk)
			apkBackupRepository.createBackup()
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

	protected fun installPatched(updating: Boolean): Operation<Unit> {
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
		val uninstallResult = apkRepository.uninstall()
		if (!uninstallResult) {
			throw UninstallException()
		}
	}

	private inline fun install(crossinline installApk: suspend () -> Result): Operation<Unit> =
		operation(progressMax = 100) {
			status(R.string.status_installing)
			installApk().onFailure { throw InstallException(it.reason) }
		}
}