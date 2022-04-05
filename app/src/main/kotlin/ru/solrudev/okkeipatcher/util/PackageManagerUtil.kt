package ru.solrudev.okkeipatcher.util

import android.content.pm.PackageManager
import android.os.Build
import ru.solrudev.okkeipatcher.OkkeiApplication

@Suppress("DEPRECATION")
val appVersionCode: Int
	get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) OkkeiApplication.context.packageManager.getPackageInfo(
		OkkeiApplication.context.packageName,
		0
	).longVersionCode.toInt() else OkkeiApplication.context.packageManager.getPackageInfo(
		OkkeiApplication.context.packageName,
		0
	).versionCode

val appVersionString: String
	get() {
		val pm = OkkeiApplication.context.packageManager
		val packageName = OkkeiApplication.context.packageName
		val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
		return info.versionName
	}

fun isPackageInstalled(packageName: String?): Boolean {
	if (packageName == null) return false
	return try {
		OkkeiApplication.context.packageManager.getPackageInfo(
			packageName,
			PackageManager.GET_ACTIVITIES
		)
		true
	} catch (e: PackageManager.NameNotFoundException) {
		false
	}
}