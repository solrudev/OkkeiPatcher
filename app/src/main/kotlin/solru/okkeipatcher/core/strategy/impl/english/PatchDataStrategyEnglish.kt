package solru.okkeipatcher.core.strategy.impl.english

import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.model.Language
import solru.okkeipatcher.core.strategy.PatchDataStrategy
import solru.okkeipatcher.data.manifest.OkkeiManifest
import solru.okkeipatcher.data.patchupdates.PatchUpdates
import solru.okkeipatcher.data.patchupdates.PatchUpdatesEnglish
import solru.okkeipatcher.utils.Preferences
import kotlin.math.round

class PatchDataStrategyEnglish : PatchDataStrategy {

	override fun patchUpdates(manifest: OkkeiManifest): PatchUpdates {
		return PatchUpdatesEnglish(
			isScriptsUpdateAvailable(manifest),
			isObbUpdateAvailable(manifest)
		)
	}

	override fun patchSizeInMb(manifest: OkkeiManifest): Double {
		val scriptsSize =
			manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.size?.div(1_048_576.0)
				?: 0.0
		val obbSize =
			manifest.patches[Language.English]?.get(PatchFile.Obb.name)?.size?.div(1_048_576.0)
				?: 0.0
		if (!patchUpdates(manifest).available) {
			return scriptsSize + obbSize
		}
		val scriptsUpdateSize = if (isScriptsUpdateAvailable(manifest)) scriptsSize else 0.0
		val obbUpdateSize = if (isObbUpdateAvailable(manifest)) obbSize else 0.0
		return round(scriptsUpdateSize + obbUpdateSize)
	}

	private fun isScriptsUpdateAvailable(manifest: OkkeiManifest): Boolean {
		if (!Preferences.get(AppKey.is_patched.name, false)) {
			return false
		}
		if (!Preferences.containsKey(FileVersionKey.scripts_version.name)) {
			Preferences.set(FileVersionKey.scripts_version.name, 1)
		}
		val currentScriptsVersion = Preferences.get(FileVersionKey.scripts_version.name, 1)
		val manifestScriptsVersion =
			manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.version ?: 1
		return manifestScriptsVersion > currentScriptsVersion
	}

	private fun isObbUpdateAvailable(manifest: OkkeiManifest): Boolean {
		if (!Preferences.get(AppKey.is_patched.name, false)) {
			return false
		}
		if (!Preferences.containsKey(FileVersionKey.obb_version.name)) {
			Preferences.set(FileVersionKey.obb_version.name, 1)
		}
		val currentObbVersion = Preferences.get(FileVersionKey.obb_version.name, 1)
		val manifestObbVersion =
			manifest.patches[Language.English]?.get(PatchFile.Obb.name)?.version ?: 1
		return manifestObbVersion > currentObbVersion
	}
}