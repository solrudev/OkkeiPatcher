package ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english

import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.impl.english.PatchFileVersionKey
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.util.Preferences
import javax.inject.Inject

class DefaultGetPatchUpdatesUseCase @Inject constructor(
	private val patchRepository: DefaultPatchRepository,
	private val preferencesRepository: PreferencesRepository
) : GetPatchUpdatesUseCase {

	override suspend fun invoke() = DefaultPatchUpdates(
		isScriptsUpdateAvailable(),
		isObbUpdateAvailable()
	)

	private suspend inline fun isScriptsUpdateAvailable(): Boolean {
		val isPatched = preferencesRepository.getIsPatched()
		if (!isPatched) {
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
		val isPatched = preferencesRepository.getIsPatched()
		if (!isPatched) {
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