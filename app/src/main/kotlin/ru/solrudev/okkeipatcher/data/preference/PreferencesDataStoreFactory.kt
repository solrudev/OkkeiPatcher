package ru.solrudev.okkeipatcher.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface PreferencesDataStoreFactory {
	fun create(name: String): DataStore<Preferences>
}

class PreferencesDataStoreFactoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : PreferencesDataStoreFactory {

	override fun create(name: String): DataStore<Preferences> {
		return PreferenceDataStoreFactory.create {
			applicationContext.preferencesDataStoreFile(name)
		}
	}
}