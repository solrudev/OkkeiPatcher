package solru.okkeipatcher.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
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

	override val progress = with(strategy) {
		merge(apk.progress, obb.progress, saveData.progress, progressPublisher.mutableProgress).shareIn(
			sharingScope,
			SharingStarted.Eagerly,
			replay = 1
		)
	}

	override val status = with(strategy) {
		merge(apk.status, obb.status, saveData.status, mutableStatus).shareIn(
			sharingScope,
			SharingStarted.Eagerly,
			replay = 1
		)
	}

	override val messages = with(strategy) {
		merge(apk.messages, obb.messages, saveData.messages, mutableMessages)
	}

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
		strategy.close()
		withContext(NonCancellable) { progressPublisher.mutableProgress.reset() }
		sharingScope.cancel()
	}

	private suspend inline fun freshPatch(processSaveData: Boolean) = strategy.run {
		if (processSaveData) {
			saveData.backup()
		}
		obb.backup()
		apk.backup()
		apk.patch()
		obb.patch()
		if (processSaveData) {
			saveData.restore()
		}
		Preferences.set(AppKey.is_patched.name, true)
	}

	private suspend inline fun update(patchUpdates: PatchUpdates) = strategy.run {
		if (patchUpdates.apkUpdatesAvailable) {
			apk.update()
		}
		if (patchUpdates.obbUpdatesAvailable) {
			obb.update()
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