package solru.okkeipatcher.core.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import solru.okkeipatcher.R
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.services.gamefile.impl.Apk
import solru.okkeipatcher.core.strategy.GameFileStrategy
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.patchupdates.PatchUpdates
import solru.okkeipatcher.exceptions.OkkeiException
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
		progressPublisher.mutableProgress
	).shareIn(GlobalScope, SharingStarted.WhileSubscribed(5000), replay = 1)

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val status = merge(
		strategy.apk.status,
		strategy.obb.status,
		strategy.saveData.status,
		mutableStatus
	).shareIn(GlobalScope, SharingStarted.WhileSubscribed(5000), replay = 1)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val messages =
		merge(strategy.apk.messages, strategy.obb.messages, strategy.saveData.messages, mutableMessages)

	suspend fun patch(processSaveData: Boolean, patchUpdates: PatchUpdates) = try {
		checkCanPatch(patchUpdates)
		if (patchUpdates.available) {
			update(patchUpdates)
		} else {
			freshPatch(processSaveData)
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_patch_success))
	} catch (e: Throwable) {
		withContext(NonCancellable) { mutableStatus.emit(LocalizedString.resource(R.string.status_aborted)) }
		throw e
	} finally {
		strategy.saveData.close()
		withContext(NonCancellable) { progressPublisher.mutableProgress.reset() }
	}

	private suspend inline fun freshPatch(processSaveData: Boolean) {
		if (processSaveData) {
			strategy.saveData.backup()
		}
		strategy.obb.backup()
		strategy.apk.backup()
		strategy.apk.patch()
		strategy.obb.patch()
		if (processSaveData) {
			strategy.saveData.restore()
		}
		Preferences.set(AppKey.is_patched.name, true)
	}

	private suspend inline fun update(patchUpdates: PatchUpdates) {
		if (patchUpdates.apkUpdatesAvailable) {
			strategy.apk.update()
		}
		if (patchUpdates.obbUpdatesAvailable) {
			strategy.obb.update()
		}
	}

	private fun checkCanPatch(patchUpdates: PatchUpdates) {
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