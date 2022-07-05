package ru.solrudev.okkeipatcher.data.repository.app

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import javax.inject.Inject

private val SIGNED_APK = stringPreferencesKey("signed_apk_hash")
private val BACKUP_APK = stringPreferencesKey("backup_apk_hash")
private val BACKUP_OBB = stringPreferencesKey("backup_obb_hash")
private val SAVE_DATA = stringPreferencesKey("save_data_hash")

class CommonFilesHashRepositoryImpl @Inject constructor(@ApplicationContext applicationContext: Context) :
	CommonFilesHashRepository {

	private val Context.dataStore by preferencesDataStore(name = "common_files_hash")
	private val preferences = applicationContext.dataStore

	override val signedApkHash = Preference(key = SIGNED_APK, defaultValue = "", preferences)
	override val backupApkHash = Preference(key = BACKUP_APK, defaultValue = "", preferences)
	override val backupObbHash = Preference(key = BACKUP_OBB, defaultValue = "", preferences)
	override val saveDataHash = Preference(key = SAVE_DATA, defaultValue = "", preferences)
}