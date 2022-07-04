package ru.solrudev.okkeipatcher.domain

import android.content.Context
import android.os.Environment
import java.io.File

private const val TWO_GB: Long = 2_147_483_648

val Context.externalDir: File
	get() {
		val externalFilesDir = getExternalFilesDir(null)
		return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED && externalFilesDir != null) {
			externalFilesDir
		} else {
			filesDir
		}
	}

val Context.backupDir: File
	get() = File(externalDir, "backup")

val Context.isEnoughSpace: Boolean
	get() = externalDir.usableSpace >= TWO_GB