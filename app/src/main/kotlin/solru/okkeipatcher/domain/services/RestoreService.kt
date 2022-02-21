package solru.okkeipatcher.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
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
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

class RestoreService @Inject constructor(private val strategy: GameFileStrategy) : ObservableServiceImpl() {

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

	private val isBackupAvailable: Boolean
		get() = with(strategy) {
			apk.backupExists && obb.backupExists
		}

	suspend fun restore(processSaveData: Boolean) = with(strategy) {
		try {
			checkCanRestore()
			if (processSaveData) {
				saveData.backup()
			}
			apk.restore()
			obb.restore()
			if (processSaveData) {
				saveData.restore()
			}
			apk.deleteBackup()
			obb.deleteBackup()
			Preferences.set(AppKey.is_patched.name, false)
		} finally {
			strategy.close()
			sharingScope.cancel()
		}
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