package solru.okkeipatcher.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.GameFileStrategy
import solru.okkeipatcher.core.base.ObservableServiceImpl
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.ServiceConfig
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class RestoreService @Inject constructor(private val strategy: GameFileStrategy) : ObservableServiceImpl() {

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

	private val isBackupAvailable: Boolean
		get() = strategy.apk.backupExists && strategy.obb.backupExists

	suspend fun restore(config: ServiceConfig) = try {
		assertCanRestore()
		if (config.processSaveData) {
			strategy.saveData.backup()
		}
		strategy.apk.restore()
		strategy.obb.restore()
		if (config.processSaveData) {
			strategy.saveData.restore()
		}
		strategy.apk.deleteBackup()
		strategy.obb.deleteBackup()
		Preferences.set(AppKey.is_patched.name, false)
		mutableStatus.emit(LocalizedString.resource(R.string.status_restore_success))
	} catch (e: Throwable) {
		strategy.saveData.clearTempFiles()
		withContext(NonCancellable) { mutableStatus.emit(LocalizedString.resource(R.string.status_aborted)) }
		throw e
	} finally {
		withContext(NonCancellable) { progressProvider.mutableProgress.reset() }
	}

	private fun assertCanRestore() {
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