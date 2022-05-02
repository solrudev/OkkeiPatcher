package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.preference.MappedPreference
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

private val HANDLE_SAVE_DATA = booleanPreferencesKey("handle_save_data")
private val IS_PATCHED = booleanPreferencesKey("is_patched")
private val PATCH_LANGUAGE = stringPreferencesKey("patch_language")

class PreferencesRepositoryImpl @Inject constructor(@ApplicationContext applicationContext: Context) :
	PreferencesRepository {

	private val Context.dataStore by preferencesDataStore(name = "okkei_preferences")
	private val preferences = applicationContext.dataStore

	override val isPatchedDao = Preference(key = IS_PATCHED, defaultValue = false, preferences)

	override val handleSaveDataDao = Preference(
		key = HANDLE_SAVE_DATA,
		defaultValue = Build.VERSION.SDK_INT < Build.VERSION_CODES.R,
		preferences
	)

	override val patchLanguageDao = MappedPreference(
		key = PATCH_LANGUAGE,
		toDataType = { language -> language.name },
		toDomainType = { name -> Language.fromString(name) },
		preferences
	)

	override suspend fun reset() {
		preferences.edit {
			it.clear()
		}
	}
}