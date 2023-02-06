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