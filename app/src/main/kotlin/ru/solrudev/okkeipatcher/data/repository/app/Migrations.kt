/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.repository.app

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import ru.solrudev.okkeipatcher.app.model.OperationMode

object Migration_Preferences_0_1 : DataMigration<Preferences> {

	private val isShizukuEnabled = booleanPreferencesKey("is_shizuku_enabled")
	private val operationMode = stringPreferencesKey("operation_mode")

	override suspend fun cleanUp() { // no-op
	}

	override suspend fun shouldMigrate(currentData: Preferences): Boolean {
		return currentData[isShizukuEnabled] != null
	}

	override suspend fun migrate(currentData: Preferences): Preferences {
		val isShizukuEnabledValue = currentData[isShizukuEnabled]!!
		val modifiedData = currentData.toMutablePreferences()
		val mode = if (isShizukuEnabledValue) OperationMode.Shizuku else OperationMode.NonRoot
		modifiedData[operationMode] = mode.value
		modifiedData.remove(isShizukuEnabled)
		return modifiedData.toPreferences()
	}
}