package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.installPackage
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.service.copy
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.domain.core.persistence.EmptyPersistable
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.core.persistence.Retrievable
import ru.solrudev.okkeipatcher.domain.model.exception.GameNotFoundException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkFile
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import java.io.File
import javax.inject.Inject

private val Context.isGameInstalled: Boolean
	get() = try {
		packageManager.getPackageInfo(GAME_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
		true
	} catch (_: PackageManager.NameNotFoundException) {
		false
	}

class ApkRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	private val packageUninstaller: PackageUninstaller,
	packageInstaller: PackageInstaller,
	commonFilesHashRepository: CommonFilesHashRepository,
	streamCopier: StreamCopier
) : ApkRepository {

	override val isInstalled: Boolean
		get() = applicationContext.isGameInstalled

	override val backupApk: ApkFile = ApkFileImpl(
		File(applicationContext.backupDir, "backup.apk"),
		applicationContext,
		streamCopier,
		packageInstaller,
		commonFilesHashRepository.backupApkHash,
		commonFilesHashRepository.backupApkHash
	)

	override val tempApk: ApkFile = ApkFileImpl(
		File(applicationContext.externalDir, "temp.apk"),
		applicationContext,
		streamCopier,
		packageInstaller,
		commonFilesHashRepository.signedApkHash,
		EmptyPersistable
	)

	override suspend fun uninstall() = packageUninstaller.uninstallPackage(GAME_PACKAGE_NAME)
}

private class ApkFileImpl(
	private val file: File,
	private val applicationContext: Context,
	private val streamCopier: StreamCopier,
	private val packageInstaller: PackageInstaller,
	private val hashRetrievable: Retrievable<String>,
	private val hashPersistable: Persistable<String>
) : ApkFile {

	override val path: String
		get() = file.absolutePath

	override val exists: Boolean
		get() = file.exists()

	override fun delete() {
		file.delete()
	}

	override suspend fun create() {
		val hash = copyInstalledApkTo(file)
		hashPersistable.persist(hash)
	}

	override suspend fun verify(): Boolean {
		val savedHash = hashRetrievable.retrieve()
		return compareHash(file, savedHash)
	}

	override suspend fun install() = packageInstaller.installPackage(file)

	private suspend inline fun compareHash(file: File, savedHash: String): Boolean {
		if (savedHash.isEmpty() || !file.exists()) {
			return false
		}
		val fileHash = streamCopier.computeHash(file)
		return fileHash == savedHash
	}

	/**
	 * @return File hash.
	 */
	private suspend inline fun copyInstalledApkTo(destinationFile: File) = try {
		if (!applicationContext.isGameInstalled) {
			throw GameNotFoundException()
		}
		val installedApkPath = applicationContext.packageManager
			.getPackageInfo(GAME_PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
		val installedApk = File(installedApkPath)
		streamCopier.copy(installedApk, destinationFile, hashing = true)
	} catch (t: Throwable) {
		destinationFile.delete()
		throw t
	}
}