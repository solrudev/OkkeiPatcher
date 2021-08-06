package solru.okkeipatcher.core

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.core.base.GameFileStrategy
import solru.okkeipatcher.core.files.base.Apk
import solru.okkeipatcher.model.dto.AppServiceConfig
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.pm.PackageManager
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatchService @Inject constructor(var strategy: GameFileStrategy) : AppServiceBase() {

	@DelicateCoroutinesApi
	@ExperimentalCoroutinesApi
	override val progress = merge(
		strategy.apk.progress,
		strategy.obb.progress,
		strategy.saveData.progress,
		progressMutable
	).shareIn(GlobalScope, SharingStarted.Lazily, replay = 1)

	@DelicateCoroutinesApi
	@ExperimentalCoroutinesApi
	override val status = merge(
		strategy.apk.status,
		strategy.obb.status,
		strategy.saveData.status,
		statusMutable
	).stateIn(GlobalScope, SharingStarted.Lazily, R.string.empty)

	@ExperimentalCoroutinesApi
	override val message =
		merge(strategy.apk.message, strategy.obb.message, strategy.saveData.message, messageMutable)

	suspend fun patch(manifest: OkkeiManifest, config: AppServiceConfig) =
		tryWrapper(onCatch = { strategy.saveData.clearTempFiles() }) {
			isRunning = true
			assertCanPatch(config.patchUpdates)
			if (config.patchUpdates.available) {
				update(manifest, config)
			} else {
				freshPatch(manifest, config)
			}
			statusMutable.emit(R.string.status_patch_success)
		}

	private suspend inline fun freshPatch(manifest: OkkeiManifest, config: AppServiceConfig) {
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

	private suspend inline fun update(manifest: OkkeiManifest, config: AppServiceConfig) {
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
			throwErrorMessage(R.string.error_patched)
		}
		if (!PackageManager.isPackageInstalled(Apk.PACKAGE_NAME)) {
			throwErrorMessage(R.string.error_game_not_found)
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throwErrorMessage(R.string.error_no_free_space)
		}
	}
}