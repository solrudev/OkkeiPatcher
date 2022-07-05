package ru.solrudev.okkeipatcher.data.util

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

val Context.externalDir: File
	get() {
		val externalFilesDir = getExternalFilesDir(null)
		return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED && externalFilesDir != null) {
			externalFilesDir
		} else {
			filesDir
		}
	}

@Suppress("DEPRECATION")
val Context.versionCode: Int
	get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
		packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
	} else {
		packageManager.getPackageInfo(packageName, 0).versionCode
	}