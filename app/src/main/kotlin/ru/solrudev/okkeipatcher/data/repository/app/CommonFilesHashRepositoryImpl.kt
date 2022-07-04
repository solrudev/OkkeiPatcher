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

	override val signedApkHash = Preference(SIGNED_APK, "", preferences)
	override val backupApkHash = Preference(BACKUP_APK, "", preferences)
	override val backupObbHash = Preference(BACKUP_OBB, "", preferences)
	override val saveDataHash = Preference(SAVE_DATA, "", preferences)
}