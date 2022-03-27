package solru.okkeipatcher.domain.service

import solru.okkeipatcher.R
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.gamefile.strategy.GameFileStrategy
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.operation.AggregateOperation
import solru.okkeipatcher.domain.operation.EmptyOperation
import solru.okkeipatcher.domain.operation.Operation
import solru.okkeipatcher.util.Preferences

class RestoreOperation(
	private val strategy: GameFileStrategy,
	private val handleSaveData: Boolean
) : Operation<Unit> {

	private val operation = object : AggregateOperation(
		with(strategy) {
			listOf(
				if (handleSaveData) saveData.backup() else EmptyOperation,
				apk.restore(),
				obb.restore(),
				if (handleSaveData) saveData.restore() else EmptyOperation
			)
		}
	) {
		override suspend fun postInvoke() = strategy.run {
			apk.deleteBackup()
			obb.deleteBackup()
			Preferences.set(AppKey.is_patched.name, false)
		}
	}

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun invoke() = strategy.use { operation() }

	/**
	 * Throws an exception if conditions for restoring are not met.
	 */
	fun checkCanRestore() {
		val isPatched = Preferences.get(AppKey.is_patched.name, false)
		if (!isPatched) {
			throw LocalizedException(LocalizedString.resource(R.string.error_not_patched))
		}
		if (!isBackupAvailable()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_backup_not_found))
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throw LocalizedException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}

	private fun isBackupAvailable() = with(strategy) {
		apk.backupExists && obb.backupExists
	}
}