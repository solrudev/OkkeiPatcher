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

package ru.solrudev.okkeipatcher.data.preference

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.util.computeIfAbsentCompat
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesDataStoreFactory {
	fun create(name: String, migrations: List<DataMigration<Preferences>> = emptyList()): DataStore<Preferences>
}

@Singleton
class PreferencesDataStoreFactoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : PreferencesDataStoreFactory {

	private val cache = ConcurrentHashMap<String, DataStore<Preferences>>()

	override fun create(name: String, migrations: List<DataMigration<Preferences>>): DataStore<Preferences> {
		return cache.computeIfAbsentCompat(name) { name ->
			PreferenceDataStoreFactory.create(migrations = migrations) {
				applicationContext.preferencesDataStoreFile(name)
			}
		}
	}
}