package ru.solrudev.okkeipatcher.domain.service

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.installPackage
import java.io.File
import javax.inject.Inject

interface PackageInstallerFacade {
	suspend fun installPackage(apkFile: File): InstallResult
	suspend fun uninstallPackage(packageName: String): Boolean
}

class PackageInstallerFacadeImpl @Inject constructor(
	private val packageInstaller: PackageInstaller,
	private val packageUninstaller: PackageUninstaller
) : PackageInstallerFacade {

	override suspend fun installPackage(apkFile: File) = packageInstaller.installPackage(apkFile)
	override suspend fun uninstallPackage(packageName: String) = packageUninstaller.uninstallPackage(packageName)
}