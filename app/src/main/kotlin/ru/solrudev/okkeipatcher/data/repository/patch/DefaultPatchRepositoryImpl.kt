package ru.solrudev.okkeipatcher.data.repository.patch

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.solrudev.okkeipatcher.data.network.api.patch.DefaultPatchApi
import ru.solrudev.okkeipatcher.data.network.model.patch.DefaultPatchDto
import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.english.PatchFileVersionKey
import ru.solrudev.okkeipatcher.io.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.util.Preferences
import javax.inject.Inject

class DefaultPatchRepositoryImpl @Inject constructor(
	private val defaultPatchApi: DefaultPatchApi,
	private val preferencesRepository: PreferencesRepository
) : DefaultPatchRepository {

	private val patchDataMutex = Mutex()
	private var patchDataCache: DefaultPatchDto? = null

	override suspend fun getPatchUpdates() = DefaultPatchUpdates(
		isScriptsUpdateAvailable(),
		isObbUpdateAvailable()
	)

	override suspend fun getPatchSizeInMb(): Double {
		try {
			val scriptsSize = getScriptsData().size / 1_048_576.0
			val obbSize = getObbData().size / 1_048_576.0
			val patchUpdates = getPatchUpdates()
			if (!patchUpdates.available) {
				return scriptsSize + obbSize
			}
			val scriptsUpdateSize = if (patchUpdates.apkUpdatesAvailable) scriptsSize else 0.0
			val obbUpdateSize = if (patchUpdates.obbUpdatesAvailable) obbSize else 0.0
			return scriptsUpdateSize + obbUpdateSize
		} catch (t: Throwable) {
			return -1.0
		}
	}

	override suspend fun getScriptsData() = retrievePatchData().scripts
	override suspend fun getObbData() = retrievePatchData().obb

	private suspend inline fun retrievePatchData(): DefaultPatchDto {
		patchDataMutex.withLock {
			patchDataCache?.let { return it }
			return defaultPatchApi.getPatchData().also { patchDataCache = it }
		}
	}

	private suspend inline fun isScriptsUpdateAvailable(): Boolean {
		val isPatched = preferencesRepository.isPatchedDao.retrieve()
		if (!isPatched) {
			return false
		}
		val currentScriptsVersion = Preferences.get(PatchFileVersionKey.scripts_version.name, 1)
		val latestScriptsVersion = try {
			getScriptsData().version
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
			getObbData().version
		} catch (_: NetworkNotAvailableException) {
			currentObbVersion
		}
		return latestObbVersion > currentObbVersion
	}
}