package ru.solrudev.okkeipatcher.data.repository.util

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.data.notification
import io.github.solrudev.simpleinstaller.installPackage
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.Result

suspend fun PackageInstaller.install(apkPath: Path, immediate: Boolean = false): Result {
	val result = installPackage(apkPath.toFile()) {
		if (immediate) {
			confirmationStrategy = ConfirmationStrategy.IMMEDIATE
		}
		notification {
			icon = R.mipmap.ic_launcher_foreground
		}
	}
	return when (result) {
		is InstallResult.Failure -> Result.failure(result.cause.toString())
		is InstallResult.Success -> Result.success()
	}
}