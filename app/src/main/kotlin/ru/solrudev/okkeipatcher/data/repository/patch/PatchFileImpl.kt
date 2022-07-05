package ru.solrudev.okkeipatcher.data.repository.patch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import ru.solrudev.okkeipatcher.data.core.InMemoryCache
import ru.solrudev.okkeipatcher.data.network.model.FileDto
import ru.solrudev.okkeipatcher.data.preference.Preference
import ru.solrudev.okkeipatcher.domain.core.persistence.Retrievable
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException

class PatchFileImpl<T>(
	private val cache: InMemoryCache<T>,
	name: String,
	private val selector: (T) -> FileDto,
	private val patchStatus: Retrievable<Boolean>,
	preferences: DataStore<Preferences>
) : PatchFile {

	private val versionKey = intPreferencesKey(name)
	override val installedVersion = Preference(versionKey, defaultValue = 1, preferences)

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