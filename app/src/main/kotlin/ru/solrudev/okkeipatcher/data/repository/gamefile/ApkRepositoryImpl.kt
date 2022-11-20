package ru.solrudev.okkeipatcher.data.repository.gamefile

import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.notification
import io.github.solrudev.simpleinstaller.uninstallPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.gamefile.paths.ApkPaths
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.GameInstallationProvider
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import javax.inject.Inject

class ApkRepositoryImpl @Inject constructor(
	apkPaths: ApkPaths,
	private val gameInstallationProvider: GameInstallationProvider,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val packageUninstaller: PackageUninstaller,
	private val packageInstaller: PackageInstaller,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val fileSystem: FileSystem
) : ApkRepository {

	override val isInstalled: Boolean
		get() = gameInstallationProvider.isInstalled()

	override val tempExists: Boolean
		get() = fileSystem.exists(temp)

	private val temp = apkPaths.temp

	private val installed: Path
		get() = gameInstallationProvider.getApkPath()

	override fun deleteTemp() {
		fileSystem.delete(temp)
	}

	override suspend fun createTemp() {
		try {
			withContext(ioDispatcher) {
				fileSystem.copy(installed, temp)
			}
		} catch (t: Throwable) {
			fileSystem.delete(temp)
			throw t
		}
	}

	override suspend fun verifyTemp(): Boolean {
		if (!fileSystem.exists(temp)) {
			return false
		}
		val savedHash = commonFilesHashRepository.signedApkHash.retrieve()
		if (savedHash.isEmpty()) {
			return false
		}
		val fileHash = withContext(ioDispatcher) { fileSystem.computeHash(temp) }
		return fileHash == savedHash
	}

	override suspend fun installTemp() = packageInstaller.install(temp.toFile())

	override suspend fun uninstall() = packageUninstaller.uninstallPackage(GAME_PACKAGE_NAME) {
		notification {
			icon = R.mipmap.ic_launcher_foreground
		}
	}
}