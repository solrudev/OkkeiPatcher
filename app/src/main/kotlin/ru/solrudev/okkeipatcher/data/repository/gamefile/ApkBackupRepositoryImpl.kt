package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageInstaller
import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.isGameInstalled
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.service.copy
import ru.solrudev.okkeipatcher.data.util.getPackageInfoCompat
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.model.exception.GameNotFoundException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import java.io.File
import javax.inject.Inject

class ApkBackupRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val packageInstaller: PackageInstaller,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val streamCopier: StreamCopier
) : ApkBackupRepository {

	override val backupExists: Boolean
		get() = backup.exists()

	private val backup = File(applicationContext.backupDir, "backup.apk")

	override fun deleteBackup() {
		backup.delete()
	}

	override suspend fun createBackup() = try {
		if (!applicationContext.isGameInstalled) {
			throw GameNotFoundException()
		}
		val installedApkPath = applicationContext.packageManager
			.getPackageInfoCompat(GAME_PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
		val installedApk = File(installedApkPath)
		val hash = streamCopier.copy(installedApk, backup, ioDispatcher, hashing = true)
		commonFilesHashRepository.backupApkHash.persist(hash)
	} catch (t: Throwable) {
		backup.delete()
		throw t
	}

	override suspend fun verifyBackup(): Boolean {
		if (!backup.exists()) {
			return false
		}
		val savedHash = commonFilesHashRepository.backupApkHash.retrieve()
		if (savedHash.isEmpty()) {
			return false
		}
		val fileHash = streamCopier.computeHash(backup, ioDispatcher)
		return fileHash == savedHash
	}

	override suspend fun installBackup() = packageInstaller.install(backup)
}