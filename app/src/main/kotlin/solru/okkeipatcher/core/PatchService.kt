package solru.okkeipatcher.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.GameFileStrategy
import solru.okkeipatcher.core.base.ObservableServiceImpl
import solru.okkeipatcher.core.files.base.Apk
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.ServiceConfig
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.isPackageInstalled
import javax.inject.Inject

class PatchService @Inject constructor(private val strategy: GameFileStrategy) : ObservableServiceImpl() {

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val progress = merge(
		strategy.apk.progress,
		strategy.obb.progress,
		strategy.saveData.progress,
		progressProvider.mutableProgress
	).shareIn(GlobalScope, SharingStarted.Eagerly, replay = 1)

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val status = merge(
		strategy.apk.status,
		strategy.obb.status,
		strategy.saveData.status,
		mutableStatus
	).shareIn(GlobalScope, SharingStarted.Eagerly, replay = 1)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val messages =
		merge(strategy.apk.messages, strategy.obb.messages, strategy.saveData.messages, mutableMessages)

	suspend fun patch(manifest: OkkeiManifest, config: ServiceConfig) = try {
		assertCanPatch(config.patchUpdates)
		if (config.patchUpdates.available) {
			update(manifest, config)
		} else {
			freshPatch(manifest, config)
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_patch_success))
	} catch (e: Throwable) {
		strategy.saveData.clearTempFiles()
		withContext(NonCancellable) { mutableStatus.emit(LocalizedString.resource(R.string.status_aborted)) }
		throw e
	} finally {
		withContext(NonCancellable) { progressProvider.mutableProgress.reset() }
	}

	private suspend inline fun freshPatch(manifest: OkkeiManifest, config: ServiceConfig) {
		if (config.processSaveData) {
			strategy.saveData.backup()
		}
		strategy.obb.backup()
		strategy.apk.backup()
		strategy.apk.patch(manifest)
		strategy.obb.patch(manifest)
		if (config.processSaveData) {
			strategy.saveData.restore()
		}
		Preferences.set(AppKey.is_patched.name, true)
	}

	private suspend inline fun update(manifest: OkkeiManifest, config: ServiceConfig) {
		if (config.patchUpdates.apkUpdates) {
			strategy.apk.update(manifest)
		}
		if (config.patchUpdates.obbUpdates) {
			strategy.obb.update(manifest)
		}
	}

	private fun assertCanPatch(patchUpdates: PatchUpdates) {
		val isPatched = Preferences.get(AppKey.is_patched.name, false)
		if (isPatched && !patchUpdates.available) {
			throw OkkeiException(LocalizedString.resource(R.string.error_patched))
		}
		if (!isPackageInstalled(Apk.PACKAGE_NAME)) {
			throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throw OkkeiException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}
}