package ru.solrudev.okkeipatcher.data.repository.gamefile.util

import android.content.Context
import android.content.pm.PackageManager
import ru.solrudev.okkeipatcher.data.util.externalDir
import java.io.File

val Context.backupDir: File
	get() = File(externalDir, "backup")

val Context.isGameInstalled: Boolean
	get() = try {
		@Suppress("DEPRECATION")
		packageManager.getPackageInfo(GAME_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
		true
	} catch (_: PackageManager.NameNotFoundException) {
		false
	}