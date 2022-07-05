package ru.solrudev.okkeipatcher.data.repository.patch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import ru.solrudev.okkeipatcher.data.core.Cache
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.domain.core.persistence.Retrievable
import ru.solrudev.okkeipatcher.domain.model.PatchFileData
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException

class PatchFileImpl<T>(
	private val cache: Cache<T>,
	name: String,
	private val selector: (T) -> PatchFileData,
	private val patchStatus: Retrievable<Boolean>,
	preferences: DataStore<Preferences>
) : PatchFile {

	private val versionKey = intPreferencesKey(name)
	override val installedVersion = Preference(key = versionKey, defaultValue = 1, preferences)

	override suspend fun getData() = selector(cache.retrieve())

	override suspend fun isUpdateAvailable(): Boolean {
		val isPatched = patchStatus.retrieve()
		if (!isPatched) {
			return false
		}
		val currentVersion = installedVersion.retrieve()
		val latestVersion = try {
			getData().version
		} catch (_: NetworkNotAvailableException) {
			currentVersion
		}
		return latestVersion > currentVersion
	}
}