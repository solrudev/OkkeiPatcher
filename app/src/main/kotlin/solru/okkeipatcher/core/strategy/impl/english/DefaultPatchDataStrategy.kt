package solru.okkeipatcher.core.strategy.impl.english

import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.strategy.PatchDataStrategy
import solru.okkeipatcher.data.patchupdates.DefaultPatchUpdates
import solru.okkeipatcher.data.patchupdates.PatchUpdates
import solru.okkeipatcher.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject
import kotlin.math.round

class DefaultPatchDataStrategy @Inject constructor(private val patchRepository: DefaultPatchRepository) :
	PatchDataStrategy {

	override suspend fun getPatchUpdates(): PatchUpdates {
		return DefaultPatchUpdates(
			isScriptsUpdateAvailable(),
			isObbUpdateAvailable()
		)
	}

	override suspend fun getPatchSizeInMb(): Double {
		val scriptsSize = patchRepository.getScriptsData().size / 1_048_576.0
		val obbSize = patchRepository.getObbData().size / 1_048_576.0
		if (!getPatchUpdates().available) {
			return scriptsSize + obbSize
		}
		val scriptsUpdateSize = if (isScriptsUpdateAvailable()) scriptsSize else 0.0
		val obbUpdateSize = if (isObbUpdateAvailable()) obbSize else 0.0
		return round(scriptsUpdateSize + obbUpdateSize)
	}

	private suspend inline fun isScriptsUpdateAvailable(): Boolean {
		if (!Preferences.get(AppKey.is_patched.name, false)) {
			return false
		}
		if (!Preferences.containsKey(FileVersionKey.scripts_version.name)) {
			Preferences.set(FileVersionKey.scripts_version.name, 1)
		}
		val currentScriptsVersion = Preferences.get(FileVersionKey.scripts_version.name, 1)
		val latestScriptsVersion = patchRepository.getScriptsData().version
		return latestScriptsVersion > currentScriptsVersion
	}

	private suspend inline fun isObbUpdateAvailable(): Boolean {
		if (!Preferences.get(AppKey.is_patched.name, false)) {
			return false
		}
		if (!Preferences.containsKey(FileVersionKey.obb_version.name)) {
			Preferences.set(FileVersionKey.obb_version.name, 1)
		}
		val currentObbVersion = Preferences.get(FileVersionKey.obb_version.name, 1)
		val manifestObbVersion = patchRepository.getObbData().version
		return manifestObbVersion > currentObbVersion
	}
}