package solru.okkeipatcher.domain.usecase.patch.impl.english

import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.gamefile.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import solru.okkeipatcher.io.exception.NetworkNotAvailableException
import solru.okkeipatcher.util.Preferences
import javax.inject.Inject

class DefaultGetPatchUpdatesUseCase @Inject constructor(private val patchRepository: DefaultPatchRepository) :
	GetPatchUpdatesUseCase {

	override suspend fun invoke() = DefaultPatchUpdates(
		isScriptsUpdateAvailable(),
		isObbUpdateAvailable()
	)

	private suspend inline fun isScriptsUpdateAvailable(): Boolean {
		if (!Preferences.get(AppKey.is_patched.name, false)) {
			return false
		}
		if (!Preferences.containsKey(PatchFileVersionKey.scripts_version.name)) {
			Preferences.set(PatchFileVersionKey.scripts_version.name, 1)
		}
		val currentScriptsVersion = Preferences.get(PatchFileVersionKey.scripts_version.name, 1)
		val latestScriptsVersion = try {
			patchRepository.getScriptsData().version
		} catch (_: NetworkNotAvailableException) {
			currentScriptsVersion
		}
		return latestScriptsVersion > currentScriptsVersion
	}

	private suspend inline fun isObbUpdateAvailable(): Boolean {
		if (!Preferences.get(AppKey.is_patched.name, false)) {
			return false
		}
		if (!Preferences.containsKey(PatchFileVersionKey.obb_version.name)) {
			Preferences.set(PatchFileVersionKey.obb_version.name, 1)
		}
		val currentObbVersion = Preferences.get(PatchFileVersionKey.obb_version.name, 1)
		val latestObbVersion = try {
			patchRepository.getObbData().version
		} catch (_: NetworkNotAvailableException) {
			currentObbVersion
		}
		return latestObbVersion > currentObbVersion
	}
}