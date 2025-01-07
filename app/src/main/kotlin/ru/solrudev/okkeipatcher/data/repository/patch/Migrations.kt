/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

@file:Suppress("ClassName")

package ru.solrudev.okkeipatcher.data.repository.patch

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

object Migration_PatchFiles_0_1 : DataMigration<Preferences> {

	private val scripts = intPreferencesKey("scripts")
	private val apk = intPreferencesKey("apk")

	override suspend fun cleanUp() {}

	override suspend fun shouldMigrate(currentData: Preferences): Boolean {
		return currentData[scripts] != null
	}

	override suspend fun migrate(currentData: Preferences): Preferences {
		val scriptsValue = currentData[scripts]!!
		val modifiedData = currentData.toMutablePreferences()
		modifiedData[apk] = scriptsValue
		modifiedData.remove(scripts)
		return modifiedData.toPreferences()
	}
}