package solru.okkeipatcher.domain.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.strategy.GameFileStrategy
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

class RestoreService @Inject constructor(private val strategy: GameFileStrategy) : ObservableServiceImpl() {

	private val sharingScope = CoroutineScope(EmptyCoroutineContext)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(
		strategy.apk.progress,
		strategy.obb.progress,
		strategy.saveData.progress,
		progressPublisher.mutableProgress
	).shareIn(sharingScope, SharingStarted.Eagerly, replay = 1)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val status = merge(
		strategy.apk.status,
		strategy.obb.status,
		strategy.saveData.status,
		mutableStatus
	).shareIn(sharingScope, SharingStarted.Eagerly, replay = 1)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val messages =
		merge(strategy.apk.messages, strategy.obb.messages, strategy.saveData.messages, mutableMessages)

	private val isBackupAvailable: Boolean
		get() = strategy.apk.backupExists && strategy.obb.backupExists

	suspend fun restore(processSaveData: Boolean) = try {
		checkCanRestore()
		if (processSaveData) {
			strategy.saveData.backup()
		}
		strategy.apk.restore()
		strategy.obb.restore()
		if (processSaveData) {
			strategy.saveData.restore()
		}
		strategy.apk.deleteBackup()
		strategy.obb.deleteBackup()
		Preferences.set(AppKey.is_patched.name, false)
		mutableStatus.emit(LocalizedString.resource(R.string.status_restore_success))
	} catch (e: Throwable) {
		withContext(NonCancellable) { mutableStatus.emit(LocalizedString.resource(R.string.status_aborted)) }
		throw e
	} finally {
		strategy.apk.close()
		strategy.saveData.close()
		withContext(NonCancellable) { progressPublisher.mutableProgress.reset() }
		sharingScope.cancel()
	}

	private fun checkCanRestore() {
		val isPatched = Preferences.get(AppKey.is_patched.name, false)
		if (!isPatched) {
			throw OkkeiException(LocalizedString.resource(R.string.error_not_patched))
		}
		if (!isBackupAvailable) {
			throw OkkeiException(LocalizedString.resource(R.string.error_backup_not_found))
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throw OkkeiException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}
}