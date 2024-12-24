/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import android.content.pm.PackageManager
import dagger.Reusable
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

@Reusable
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
			.applicationInfo!!
			.publicSourceDir
			.toPath()
	}
}