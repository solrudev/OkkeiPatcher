package ru.solrudev.okkeipatcher.data.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

@Suppress("DEPRECATION")
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int): PackageInfo {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
	} else {
		getPackageInfo(packageName, flags)
	}
}