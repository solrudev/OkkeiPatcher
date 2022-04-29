package ru.solrudev.okkeipatcher.domain.util.extension

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

@Suppress("DEPRECATION")
val Context.versionCode: Int
	get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
		packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
	} else {
		packageManager.getPackageInfo(packageName, 0).versionCode
	}

fun Context.isPackageInstalled(packageName: String) = try {
	packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
	true
} catch (_: PackageManager.NameNotFoundException) {
	false
}