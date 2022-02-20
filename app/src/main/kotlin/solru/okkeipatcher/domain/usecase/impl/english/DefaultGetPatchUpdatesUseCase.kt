package solru.okkeipatcher.domain.usecase.impl.english

import solru.okkeipatcher.data.patchupdates.DefaultPatchUpdates
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.domain.usecase.GetPatchUpdatesUseCase
import solru.okkeipatcher.io.exceptions.NetworkNotAvailableException
import solru.okkeipatcher.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.utils.Preferences
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