package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller
import io.github.solrudev.simpleinstaller.data.notification
import io.github.solrudev.simpleinstaller.uninstallPackage
import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.isGameInstalled
import ru.solrudev.okkeipatcher.data.repository.util.install
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.service.copy
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.model.exception.GameNotFoundException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import java.io.File
import javax.inject.Inject

class ApkRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val packageUninstaller: PackageUninstaller,
	private val packageInstaller: PackageInstaller,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val streamCopier: StreamCopier
) : ApkRepository {

	override val isInstalled: Boolean
		get() = applicationContext.isGameInstalled

	override val tempPath: String
		get() = temp.absolutePath

	override val tempExists: Boolean
		get() = temp.exists()

	private val temp = File(applicationContext.externalDir, "temp.apk")

	override fun deleteTemp() {
		temp.delete()
	}

	override suspend fun createTemp() {
		try {
			if (!applicationContext.isGameInstalled) {
				throw GameNotFoundException()
			}
			@Suppress("DEPRECATION")
			val installedApkPath = applicationContext.packageManager
				.getPackageInfo(GAME_PACKAGE_NAME, 0)
				.applicationInfo
				.publicSourceDir
			val installedApk = File(installedApkPath)
			streamCopier.copy(installedApk, temp, ioDispatcher)
		} catch (t: Throwable) {
			temp.delete()
			throw t
		}
	}

	override suspend fun verifyTemp(): Boolean {
		if (!temp.exists()) {
			return false
		}
		val savedHash = commonFilesHashRepository.signedApkHash.retrieve()
		if (savedHash.isEmpty()) {
			return false
		}
		val fileHash = streamCopier.computeHash(temp, ioDispatcher)
		return fileHash == savedHash
	}

	override suspend fun installTemp() = packageInstaller.install(temp)

	override suspend fun uninstall() = packageUninstaller.uninstallPackage(GAME_PACKAGE_NAME) {
		notification {
			icon = R.mipmap.ic_launcher_foreground
		}
	}
}