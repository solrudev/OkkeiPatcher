package ru.solrudev.okkeipatcher.data.repository.util

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.installPackage
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import java.io.File

suspend fun PackageInstaller.install(file: File) = when (val result = installPackage(file)) {
	is InstallResult.Failure -> Result.Failure(
		LocalizedString.raw(result.cause.toString())
	)
	is InstallResult.Success -> Result.Success
}