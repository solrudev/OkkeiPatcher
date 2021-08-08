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
import solru.okkeipatcher.model.dto.AppServiceConfig
import solru.okkeipatcher.utils.Preferences
import javax.inject.Inject

class RestoreService @Inject constructor(private val strategy: GameFileStrategy) :
	AppServiceBase() {

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val progress = merge(
		strategy.apk.progress,
		strategy.obb.progress,
		strategy.saveData.progress,
		progressMutable
	).shareIn(GlobalScope, SharingStarted.Lazily, replay = 1)

	@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
	override val status = merge(
		strategy.apk.status,
		strategy.obb.status,
		strategy.saveData.status,
		statusMutable
	).stateIn(GlobalScope, SharingStarted.Lazily, R.string.empty)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val message =
		merge(strategy.apk.message, strategy.obb.message, strategy.saveData.message, messageMutable)

	private val isBackupAvailable: Boolean
		get() = strategy.apk.backupExists && strategy.obb.backupExists

	suspend fun restore(config: AppServiceConfig) =
		tryWrapper(onCatch = { strategy.saveData.clearTempFiles() }) {
			isRunning = true
			assertCanRestore()
			if (config.processSaveData) {
				strategy.saveData.backup()
			}
			strategy.apk.restore()
			strategy.obb.restore()
			if (config.processSaveData) {
				strategy.saveData.restore()
			}
			Preferences.set(AppKey.is_patched.name, false)
			statusMutable.emit(R.string.status_restore_success)
		}

	private fun assertCanRestore() {
		val isPatched = Preferences.get(AppKey.is_patched.name, false)
		if (!isPatched) {
			throwErrorMessage(R.string.error_not_patched)
		}
		if (!isBackupAvailable) {
			throwErrorMessage(R.string.error_backup_not_found)
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throwErrorMessage(R.string.error_no_free_space)
		}
	}
}