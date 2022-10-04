package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.preference.MappedPreference
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PermissionsRepository
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

private val HANDLE_SAVE_DATA = booleanPreferencesKey("handle_save_data")
private val IS_PATCHED = booleanPreferencesKey("is_patched")
private val PATCH_LANGUAGE = stringPreferencesKey("patch_language")
private val PATCH_VERSION = stringPreferencesKey("patch_version")

class PreferencesRepositoryImpl @Inject constructor(
	@ApplicationContext applicationContext: Context,
	permissionsRepository: PermissionsRepository
) : PreferencesRepository {

	private val Context.dataStore by preferencesDataStore(name = "okkei_preferences")
	private val preferences = applicationContext.dataStore

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

	override val patchVersion = Preference(key = PATCH_VERSION, defaultValue = { "-" }, preferences)

	override suspend fun reset() {
		preferences.edit {
			it.clear()
		}
	}
}