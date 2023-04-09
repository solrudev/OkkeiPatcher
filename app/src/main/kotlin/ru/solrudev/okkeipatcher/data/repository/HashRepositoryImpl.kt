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

package ru.solrudev.okkeipatcher.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import javax.inject.Inject

private val SIGNED_APK = stringPreferencesKey("signed_apk_hash")
private val BACKUP_APK = stringPreferencesKey("backup_apk_hash")
private val BACKUP_OBB = stringPreferencesKey("backup_obb_hash")
private val SAVE_DATA = stringPreferencesKey("save_data_hash")

class HashRepositoryImpl @Inject constructor(
	preferencesDataStoreFactory: PreferencesDataStoreFactory
) : HashRepository {

	private val preferences = preferencesDataStoreFactory.create("files_hash")
	override val signedApkHash = Preference(key = SIGNED_APK, defaultValue = { "" }, preferences)
	override val backupApkHash = Preference(key = BACKUP_APK, defaultValue = { "" }, preferences)
	override val backupObbHash = Preference(key = BACKUP_OBB, defaultValue = { "" }, preferences)
	override val saveDataHash = Preference(key = SAVE_DATA, defaultValue = { "" }, preferences)

	override suspend fun clear() {
		preferences.edit {
			it.clear()
		}
	}
}