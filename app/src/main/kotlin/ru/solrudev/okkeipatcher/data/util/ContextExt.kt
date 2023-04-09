/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.util

import android.content.Context
import android.content.pm.PackageManager
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
		packageManager.getPackageInfoCompat(packageName, 0).longVersionCode.toInt()
	} else {
		packageManager.getPackageInfoCompat(packageName, 0).versionCode
	}

val Context.versionName: String
	get() = packageManager.getPackageInfoCompat(packageName, PackageManager.GET_META_DATA).versionName