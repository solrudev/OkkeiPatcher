package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toPath
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.getPackageInfoCompat
import ru.solrudev.okkeipatcher.domain.model.exception.GameNotFoundException
import javax.inject.Inject

interface GameInstallationProvider {
	fun isInstalled(): Boolean
	fun getApkPath(): Path
}

class GameInstallationProviderImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : GameInstallationProvider {

	override fun isInstalled(): Boolean {
		return try {
			applicationContext.packageManager.getPackageInfoCompat(GAME_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
			true
		} catch (_: PackageManager.NameNotFoundException) {
			false
		}
	}

	override fun getApkPath(): Path {
		if (!isInstalled()) {
			throw GameNotFoundException()
		}
		return applicationContext.packageManager
			.getPackageInfoCompat(GAME_PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
			.toPath()
	}
}