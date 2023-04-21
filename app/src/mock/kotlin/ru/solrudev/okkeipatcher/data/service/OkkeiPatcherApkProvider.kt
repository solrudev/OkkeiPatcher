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

package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toPath
import ru.solrudev.okkeipatcher.data.util.getPackageInfoCompat
import javax.inject.Inject

interface OkkeiPatcherApkProvider {
	fun getOkkeiPatcherApkPath(): Path
}

@Reusable
class OkkeiPatcherApkProviderImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : OkkeiPatcherApkProvider {

	override fun getOkkeiPatcherApkPath(): Path {
		return applicationContext.packageManager
			.getPackageInfoCompat(applicationContext.packageName, 0)
			.applicationInfo
			.publicSourceDir
			.toPath()
	}
}