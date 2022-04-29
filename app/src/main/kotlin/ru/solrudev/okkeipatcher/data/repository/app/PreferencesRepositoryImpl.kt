package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

	override suspend fun getIsPatched() = preferences.data
		.map { it[IS_PATCHED] ?: false }
		.first()

	override suspend fun getHandleSaveData() = preferences.data
		.map { it[HANDLE_SAVE_DATA] ?: (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) }
		.first()

	override suspend fun getPatchLanguage() = preferences.data
		.map { prefs -> Language.fromString(prefs[PATCH_LANGUAGE]) }
		.first()

	override suspend fun setIsPatched(isPatched: Boolean) {
		preferences.edit {
			it[IS_PATCHED] = isPatched
		}
	}

	override suspend fun setHandleSaveData(handleSaveData: Boolean) {
		preferences.edit {
			it[HANDLE_SAVE_DATA] = handleSaveData
		}
	}

	override suspend fun setPatchLanguage(language: Language) {
		preferences.edit {
			it[PATCH_LANGUAGE] = language.name
		}
	}

	override suspend fun reset() {
		preferences.edit {
			it.clear()
		}
	}
}