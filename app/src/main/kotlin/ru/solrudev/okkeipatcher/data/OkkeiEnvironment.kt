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

package ru.solrudev.okkeipatcher.data

import android.content.Context
import android.os.Environment
import androidx.core.os.ConfigurationCompat
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toOkioPath
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.data.util.versionCode
import java.util.Locale
import javax.inject.Inject

interface OkkeiEnvironment {
	val locale: Locale
	val versionCode: Int
	val filesPath: Path
	val externalFilesPath: Path
	val externalStoragePath: Path
}

@Reusable
class OkkeiEnvironmentImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : OkkeiEnvironment {

	override val locale: Locale
		get() = ConfigurationCompat.getLocales(applicationContext.resources.configuration)[0] ?: Locale.ENGLISH

	override val versionCode: Int
		get() = applicationContext.versionCode

	override val filesPath: Path
		get() = applicationContext.filesDir.toOkioPath()

	override val externalFilesPath: Path
		get() = applicationContext.externalDir.toOkioPath()

	override val externalStoragePath: Path
		get() = Environment.getExternalStorageDirectory().toOkioPath()
}