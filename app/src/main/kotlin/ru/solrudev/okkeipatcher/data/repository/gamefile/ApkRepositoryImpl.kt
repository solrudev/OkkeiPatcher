package ru.solrudev.okkeipatcher.data.repository.gamefile

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.notification
import io.github.solrudev.simpleinstaller.uninstallPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.GameInstallationProvider
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactory
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.service.ZipPackage
import javax.inject.Inject

class ApkRepositoryImpl @Inject constructor(
	environment: OkkeiEnvironment,
	private val gameInstallationProvider: GameInstallationProvider,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val packageUninstaller: PackageUninstaller,
	private val packageInstaller: PackageInstaller,
	private val hashRepository: HashRepository,
	private val apkZipPackageFactory: ApkZipPackageFactory,
	private val fileSystem: FileSystem
) : ApkRepository {

	override val isInstalled: Boolean
		get() = gameInstallationProvider.isInstalled()

	override val tempExists: Boolean
		get() = fileSystem.exists(temp)

	private val temp = environment.externalFilesPath / "temp.apk"

	private val installed: Path
		get() = gameInstallationProvider.getApkPath()

	override fun deleteTemp() {
		fileSystem.delete(temp)
	}

	override suspend fun createTemp(): ZipPackage {
		try {
			if (!fileSystem.exists(temp)) {
				runInterruptible(ioDispatcher) {
					fileSystem.copy(installed, temp)
				}
			}
			return apkZipPackageFactory.create(temp)
		} catch (t: Throwable) {
			fileSystem.delete(temp)
			throw t
		}
	}

	override suspend fun verifyTemp(): Boolean {
		if (!fileSystem.exists(temp)) {
			return false
		}
		val savedHash = hashRepository.signedApkHash.retrieve()
		if (savedHash.isEmpty()) {
			return false
		}
		val fileHash = runInterruptible(ioDispatcher) {
			fileSystem.computeHash(temp)
		}
		return fileHash == savedHash
	}

	override suspend fun installTemp() = packageInstaller.install(temp.toFile())

	override suspend fun uninstall() = packageUninstaller.uninstallPackage(GAME_PACKAGE_NAME) {
		notification {
			icon = R.mipmap.ic_launcher_foreground
		}
	}
}