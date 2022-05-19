package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.domain.core.operation.AggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.EmptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.PatchStrategy

class PatchOperation(
	private val strategy: PatchStrategy,
	private val handleSaveData: Boolean,
	private val patchUpdates: PatchUpdates,
	private val isPatchedDao: Dao<Boolean>
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
	suspend fun checkCanPatch() = with(strategy) {
		val isPatched = isPatchedDao.retrieve()
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

	private fun patch() = AggregateOperation(
		operations = with(strategy) {
			listOf(
				if (handleSaveData) saveData.backup() else EmptyOperation,
				obb.backup(),
				apk.backup(),
				apk.patch(),
				obb.patch(),
				if (handleSaveData) saveData.restore() else EmptyOperation
			)
		},
		doAfter = {
			isPatchedDao.persist(true)
		}
	)

	private fun update() = AggregateOperation(
		with(strategy) {
			listOf(
				if (patchUpdates.apkUpdatesAvailable) apk.update() else EmptyOperation,
				if (patchUpdates.obbUpdatesAvailable) obb.update() else EmptyOperation
			)
		}
	)
}