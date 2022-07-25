package ru.solrudev.okkeipatcher.domain.service.gamefile

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.ApkNotFoundException
import ru.solrudev.okkeipatcher.domain.model.exception.InstallException
import ru.solrudev.okkeipatcher.domain.model.exception.NotTrustworthyApkException
import ru.solrudev.okkeipatcher.domain.model.exception.UninstallException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkFile
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository

abstract class Apk(protected val apkRepository: ApkRepository) : PatchableGameFile {

	override val backupExists: Boolean
		get() = apkRepository.backupApk.exists

	override fun canPatch(): Result {
		val canInstallPatchedApk = backupExists && apkRepository.tempApk.exists
		if (!apkRepository.isInstalled && !canInstallPatchedApk) {
			return Result.Failure(
				LocalizedString.resource(R.string.error_game_not_found)
			)
		}
		return Result.Success
	}

	override fun deleteBackup() = apkRepository.backupApk.delete()

	override fun backup() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_comparing_apk))
		if (!apkRepository.backupApk.verify()) {
			status(LocalizedString.resource(R.string.status_backing_up_apk))
			apkRepository.backupApk.create()
		}
	}

	override fun restore(): Operation<Unit> {
		val apk = apkRepository.backupApk
		val uninstallOperation = uninstall(updating = false)
		val installBackupOperation = install(apk)
		return operation(uninstallOperation, installBackupOperation) {
			if (!apk.exists) {
				throw ApkNotFoundException()
			}
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (!apk.verify()) {
				throw NotTrustworthyApkException()
			}
			uninstallOperation()
			installBackupOperation()
		}
	}

	protected fun installPatched(updating: Boolean): Operation<Unit> {
		val apk = apkRepository.tempApk
		val uninstallOperation = uninstall(updating)
		val installOperation = install(apk)
		return operation(uninstallOperation, installOperation) {
			if (!apk.exists) {
				throw ApkNotFoundException()
			}
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (!apk.verify()) {
				apk.delete()
				throw NotTrustworthyApkException()
			}
			uninstallOperation()
			installOperation()
			apk.delete()
		}
	}

	private fun uninstall(updating: Boolean) = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_uninstalling))
		if (updating || !apkRepository.isInstalled) {
			return@operation
		}
		val uninstallResult = apkRepository.uninstall()
		if (!uninstallResult) {
			throw UninstallException()
		}
	}

	private fun install(apk: ApkFile): Operation<Unit> = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_installing))
		apk
			.install()
			.onFailure { throw InstallException(it.reason) }
	}
}