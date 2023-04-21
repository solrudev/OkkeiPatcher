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

package ru.solrudev.okkeipatcher.data.repository.app

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import ru.solrudev.okkeipatcher.app.model.Theme
import ru.solrudev.okkeipatcher.app.repository.PermissionsRepository
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.preference.MappedPreference
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import javax.inject.Inject
import javax.inject.Singleton

private val HANDLE_SAVE_DATA = booleanPreferencesKey("handle_save_data")
private val IS_PATCHED = booleanPreferencesKey("is_patched")
private val PATCH_LANGUAGE = stringPreferencesKey("patch_language")
private val PATCH_VERSION = stringPreferencesKey("patch_version")
private val THEME = intPreferencesKey("theme")

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
	preferencesDataStoreFactory: PreferencesDataStoreFactory,
	permissionsRepository: PermissionsRepository
) : PreferencesRepository {

	private val preferences = preferencesDataStoreFactory.create("okkei_preferences")
	override val patchStatus = Preference(key = IS_PATCHED, defaultValue = { false }, preferences)

	override val handleSaveData = Preference(
		key = HANDLE_SAVE_DATA,
		defaultValue = permissionsRepository::isSaveDataAccessGranted,
		preferences
	)

	override val patchLanguage = MappedPreference(
		key = PATCH_LANGUAGE,
		toDataType = Language::name,
		toDomainType = Language::fromString,
		preferences
	)

	override val patchVersion = Preference(key = PATCH_VERSION, defaultValue = { "" }, preferences)

	override val theme = MappedPreference(
		key = THEME,
		toDataType = Theme::ordinal,
		toDomainType = Theme::fromOrdinal,
		preferences
	)

	override suspend fun reset() {
		preferences.edit { mutablePreferences ->
			val savedTheme = mutablePreferences[THEME]
			mutablePreferences.clear()
			savedTheme?.let { theme ->
				mutablePreferences[THEME] = theme
			}
		}
	}
}