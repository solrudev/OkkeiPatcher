package solru.okkeipatcher.domain.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.patchupdates.PatchUpdates
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.services.gamefile.Apk
import solru.okkeipatcher.domain.strategy.GameFileStrategy
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

class PatchService @Inject constructor(private val strategy: GameFileStrategy) : ObservableServiceImpl() {

	private val sharingScope = CoroutineScope(EmptyCoroutineContext)

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val progress = merge(
		strategy.apk.progress,
		strategy.obb.progress,
		strategy.saveData.progress,
		progressPublisher.mutableProgress
	).shareIn(sharingScope, SharingStarted.Eagerly, replay = 1)

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val status = merge(
		strategy.apk.status,
		strategy.obb.status,
		strategy.saveData.status,
		mutableStatus
	).shareIn(sharingScope, SharingStarted.Eagerly, replay = 1)

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
		strategy.apk.close()
		strategy.saveData.close()
		withContext(NonCancellable) { progressPublisher.mutableProgress.reset() }
		sharingScope.cancel()
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
		if (!Apk.isInstalled) {
			throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throw OkkeiException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}
}