package solru.okkeipatcher.domain.service

import solru.okkeipatcher.R
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.gamefile.strategy.GameFileStrategy
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.patchupdates.PatchUpdates
import solru.okkeipatcher.domain.operation.AggregateOperation
import solru.okkeipatcher.domain.operation.EmptyOperation
import solru.okkeipatcher.domain.operation.Operation
import solru.okkeipatcher.util.Preferences

class PatchOperation(
	private val strategy: GameFileStrategy,
	private val handleSaveData: Boolean,
	private val patchUpdates: PatchUpdates
) : Operation<Unit> {

	private val operation = if (patchUpdates.available) update() else patch()
	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun invoke() = strategy.use { operation() }

	/**
	 * Throws an exception if conditions for patch are not met.
	 */
	fun checkCanPatch() = with(strategy) {
		val isPatched = Preferences.get(AppKey.is_patched.name, false)
		if (isPatched && !patchUpdates.available) {
			throw LocalizedException(LocalizedString.resource(R.string.error_patched))
		}
		apk.canPatch { failMessage ->
			throw LocalizedException(failMessage)
		}
		obb.canPatch { failMessage ->
			throw LocalizedException(failMessage)
		}
		if (!OkkeiStorage.isEnoughSpace) {
			throw LocalizedException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}

	private fun patch() = object : AggregateOperation(
		with(strategy) {
			listOf(
				if (handleSaveData) saveData.backup() else EmptyOperation,
				obb.backup(),
				apk.backup(),
				apk.patch(),
				obb.patch(),
				if (handleSaveData) saveData.restore() else EmptyOperation
			)
		}
	) {
		override suspend fun postInvoke() {
			Preferences.set(AppKey.is_patched.name, true)
		}
	}

	private fun update() = AggregateOperation(
		with(strategy) {
			listOf(
				if (patchUpdates.apkUpdatesAvailable) apk.update() else EmptyOperation,
				if (patchUpdates.obbUpdatesAvailable) obb.update() else EmptyOperation
			)
		}
	)
}