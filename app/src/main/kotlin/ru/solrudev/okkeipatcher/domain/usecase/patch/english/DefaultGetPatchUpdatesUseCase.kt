package ru.solrudev.okkeipatcher.domain.usecase.patch.english

import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.english.PatchFileVersionKey
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
		val isPatched = preferencesRepository.isPatchedDao.retrieve()
		if (!isPatched) {
			return false
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
		val isPatched = preferencesRepository.isPatchedDao.retrieve()
		if (!isPatched) {
			return false
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