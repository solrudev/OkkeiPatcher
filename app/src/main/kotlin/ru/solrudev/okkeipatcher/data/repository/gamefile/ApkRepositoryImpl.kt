package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageUninstaller
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.isPackageInstalled
import ru.solrudev.okkeipatcher.domain.backupDir
import ru.solrudev.okkeipatcher.domain.core.persistence.EmptyPersistable
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.core.persistence.Retrievable
import ru.solrudev.okkeipatcher.domain.externalDir
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkFile
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import ru.solrudev.okkeipatcher.domain.service.computeHash
import ru.solrudev.okkeipatcher.domain.service.copy
import java.io.File
import javax.inject.Inject

private const val PACKAGE_NAME = "com.mages.chaoschild_jp"

class ApkRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	private val packageUninstaller: PackageUninstaller,
	commonFilesHashRepository: CommonFilesHashRepository,
	streamCopier: StreamCopier
) : ApkRepository {

	override val isInstalled: Boolean
		get() = applicationContext.isPackageInstalled(PACKAGE_NAME)

	override val backupApk: ApkFile = ApkFileImpl(
		File(applicationContext.backupDir, "backup.apk"),
		applicationContext,
		streamCopier,
		commonFilesHashRepository.backupApkHash,
		commonFilesHashRepository.backupApkHash
	)

	override val tempApk: ApkFile = ApkFileImpl(
		File(applicationContext.externalDir, "temp.apk"),
		applicationContext,
		streamCopier,
		commonFilesHashRepository.signedApkHash,
		EmptyPersistable
	)

	override suspend fun uninstall() = packageUninstaller.uninstallPackage(PACKAGE_NAME)
}

private class ApkFileImpl(
	override val file: File,
	private val applicationContext: Context,
	private val streamCopier: StreamCopier,
	private val hashRetrievable: Retrievable<String>,
	private val hashPersistable: Persistable<String>
) : ApkFile {

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

	private suspend fun compareHash(file: File, savedHash: String): Boolean {
		if (savedHash.isEmpty() || !file.exists()) {
			return false
		}
		val fileHash = streamCopier.computeHash(file)
		return fileHash == savedHash
	}

	/**
	 * @return File hash.
	 */
	private suspend fun copyInstalledApkTo(destinationFile: File) = try {
		if (!applicationContext.isPackageInstalled(PACKAGE_NAME)) {
			throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
		}
		val installedApkPath = applicationContext.packageManager
			.getPackageInfo(PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
		val installedApk = File(installedApkPath)
		streamCopier.copy(installedApk, destinationFile, hashing = true)
	} catch (t: Throwable) {
		destinationFile.delete()
		throw t
	}
}