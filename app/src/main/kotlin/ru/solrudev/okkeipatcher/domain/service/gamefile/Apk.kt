package ru.solrudev.okkeipatcher.domain.service.gamefile

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.installPackage
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import java.io.File

abstract class Apk(
	protected val apkRepository: ApkRepository,
	private val apkZipPackage: ZipPackage,
	private val packageInstaller: PackageInstaller
) : PatchableGameFile {

	override val backupExists: Boolean
		get() = apkRepository.backupApk.exists

	override fun canPatch(onNegative: (LocalizedString) -> Unit): Boolean {
		val canInstallPatchedApk = backupExists && apkRepository.tempApk.exists
		if (!apkRepository.isInstalled && canInstallPatchedApk) {
			return true
		}
		return if (apkRepository.isInstalled) {
			true
		} else {
			onNegative(LocalizedString.resource(R.string.error_game_not_found))
			false
		}
	}

	override fun close() = apkZipPackage.close()
	override fun deleteBackup() = apkRepository.backupApk.delete()

	override fun backup() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_backing_up_apk))
		apkRepository.backupApk.create()
	}

	override fun restore(): Operation<Unit> {
		val uninstallOperation = uninstall(updating = false)
		val installBackupOperation = install {
			if (!apkRepository.backupApk.verify()) {
				throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
			}
			apkRepository.backupApk.path
		}
		return aggregateOperation(uninstallOperation, installBackupOperation)
	}

	protected fun installPatched(updating: Boolean): Operation<Unit> {
		val uninstallOperation = uninstall(updating)
		val installOperation = install { apkRepository.tempApk.path }
		return operation(uninstallOperation, installOperation) {
			if (!apkRepository.tempApk.exists) {
				throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
			}
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (!apkRepository.tempApk.verify()) {
				apkRepository.tempApk.delete()
				throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
			}
			uninstallOperation()
			installOperation()
			apkRepository.tempApk.delete()
		}
	}

	private fun uninstall(updating: Boolean) = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_uninstalling))
		if (updating || !apkRepository.isInstalled) {
			return@operation
		}
		val uninstallResult = apkRepository.uninstall()
		if (!uninstallResult) {
			throw LocalizedException(LocalizedString.resource(R.string.error_uninstall))
		}
	}

	private fun install(apkPathFactory: suspend () -> String) = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_installing))
		val apk = File(apkPathFactory())
		if (!apk.exists()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
		}
		val installResult = packageInstaller.installPackage(apk)
		if (installResult is InstallResult.Failure) {
			throw LocalizedException(
				LocalizedString.resource(
					R.string.error_install,
					installResult.cause.toString()
				)
			)
		}
	}
}