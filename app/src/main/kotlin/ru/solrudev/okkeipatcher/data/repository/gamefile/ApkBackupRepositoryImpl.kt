package ru.solrudev.okkeipatcher.data.repository.gamefile

import io.github.solrudev.simpleinstaller.PackageInstaller
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.data.repository.gamefile.paths.ApkPaths
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.GameInstallationProvider
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.data.util.copy
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.app.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import javax.inject.Inject

class ApkBackupRepositoryImpl @Inject constructor(
	apkPaths: ApkPaths,
	private val gameInstallationProvider: GameInstallationProvider,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val packageInstaller: PackageInstaller,
	private val hashRepository: HashRepository,
	private val fileSystem: FileSystem
) : ApkBackupRepository {

	override val backupExists: Boolean
		get() = fileSystem.exists(backup)

	private val backup = apkPaths.backup

	private val installed: Path
		get() = gameInstallationProvider.getApkPath()

	override fun deleteBackup() {
		fileSystem.delete(backup)
	}

	override suspend fun createBackup() = try {
		val hash = withContext(ioDispatcher) { fileSystem.copy(installed, backup, hashing = true) }
		hashRepository.backupApkHash.persist(hash)
	} catch (t: Throwable) {
		fileSystem.delete(backup)
		throw t
	}

	override suspend fun verifyBackup(): Boolean {
		if (!fileSystem.exists(backup)) {
			return false
		}
		val savedHash = hashRepository.backupApkHash.retrieve()
		if (savedHash.isEmpty()) {
			return false
		}
		val fileHash = withContext(ioDispatcher) { fileSystem.computeHash(backup) }
		return fileHash == savedHash
	}

	override suspend fun installBackup() = packageInstaller.install(backup.toFile())
}