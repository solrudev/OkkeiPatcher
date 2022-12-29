package ru.solrudev.okkeipatcher.data.repository.app

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import ru.solrudev.okkeipatcher.data.preference.MappedPreference
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.data.preference.PreferencesDataStoreFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.model.Theme
import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

private val HANDLE_SAVE_DATA = booleanPreferencesKey("handle_save_data")
private val IS_PATCHED = booleanPreferencesKey("is_patched")
private val PATCH_LANGUAGE = stringPreferencesKey("patch_language")
private val PATCH_VERSION = stringPreferencesKey("patch_version")
private val THEME = intPreferencesKey("theme")

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
		preferences.edit {
			it.clear()
		}
	}
}