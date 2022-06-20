package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageUninstaller
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.backupDir
import ru.solrudev.okkeipatcher.domain.externalDir
import ru.solrudev.okkeipatcher.domain.file.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.AbstractApkFile
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import ru.solrudev.okkeipatcher.io.service.computeHash
import ru.solrudev.okkeipatcher.io.util.extension.recreate
import ru.solrudev.okkeipatcher.util.Preferences
import java.io.File
import javax.inject.Inject

private const val PACKAGE_NAME = "com.mages.chaoschild_jp"

class ApkRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	private val streamCopier: StreamCopier,
	private val packageUninstaller: PackageUninstaller
) : ApkRepository {

	override val isInstalled: Boolean
		get() = try {
			applicationContext.packageManager.getPackageInfo(
				PACKAGE_NAME,
				PackageManager.GET_ACTIVITIES
			)
			true
		} catch (_: PackageManager.NameNotFoundException) {
			false
		}

	override val backupApk = object : AbstractApkFile(
		File(applicationContext.backupDir, "backup.apk")
	) {

		override suspend fun create() {
			val hash = copyInstalledApkTo(file, hashing = true)
			Preferences.set(CommonFileHashKey.backup_apk_hash.name, hash)
		}

		override suspend fun verify() = verify(file, CommonFileHashKey.backup_apk_hash.name)
	}

	override val tempApk = object : AbstractApkFile(
		File(applicationContext.externalDir, "temp.apk")
	) {

		override suspend fun create() {
			copyInstalledApkTo(file)
		}

		override suspend fun verify() = verify(file, CommonFileHashKey.signed_apk_hash.name)
	}

	override suspend fun uninstall() = packageUninstaller.uninstallPackage(PACKAGE_NAME)

	private suspend fun verify(file: File, key: String): Boolean {
		val savedHash = Preferences.get(key, "")
		if (savedHash.isEmpty() || !file.exists()) {
			return false
		}
		val fileHash = streamCopier.computeHash(file.inputStream(), file.length())
		return fileHash == savedHash
	}

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	private suspend fun copyInstalledApkTo(destinationFile: File, hashing: Boolean = false): String {
		try {
			if (!isInstalled) {
				throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
			}
			val installedApkPath = applicationContext.packageManager
				.getPackageInfo(PACKAGE_NAME, 0)
				.applicationInfo
				.publicSourceDir
			val installedApk = File(installedApkPath)
			val input = installedApk.inputStream()
			destinationFile.recreate()
			val output = destinationFile.outputStream()
			return streamCopier.copy(input, output, installedApk.length(), hashing)
		} catch (t: Throwable) {
			destinationFile.delete()
			throw t
		}
	}
}