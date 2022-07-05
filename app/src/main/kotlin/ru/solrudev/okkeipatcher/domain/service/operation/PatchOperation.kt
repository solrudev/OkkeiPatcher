package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.emptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.PatchStrategy

class PatchOperation(
	private val strategy: PatchStrategy,
	private val handleSaveData: Boolean,
	private val patchUpdates: PatchUpdates,
	private val patchStatus: Dao<Boolean>,
	private val storageChecker: StorageChecker
) : Operation<Unit> {

	private val operation = if (patchUpdates.available) update() else patch()
	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun invoke() = strategy.use {
		operation()
	}

	/**
	 * Throws an exception if conditions for patch are not met.
	 */
	suspend fun checkCanPatch() = with(strategy) {
		val isPatched = patchStatus.retrieve()
		if (isPatched && !patchUpdates.available) {
			throw LocalizedException(LocalizedString.resource(R.string.error_patched))
		}
		apk.checkCanPatch()
		obb.checkCanPatch()
		if (!storageChecker.isEnoughSpace()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_no_free_space))
		}
	}

	private fun patch() = with(strategy) {
		aggregateOperation(
			if (handleSaveData) saveData.backup() else emptyOperation(),
			obb.backup(),
			apk.backup(),
			apk.patch(),
			obb.patch(),
			if (handleSaveData) saveData.restore() else emptyOperation(),
			operation {
				patchStatus.persist(true)
			}
		)
	}

	private fun update() = with(strategy) {
		aggregateOperation(
			if (patchUpdates.apkUpdatesAvailable) apk.update() else emptyOperation(),
			if (patchUpdates.obbUpdatesAvailable) obb.update() else emptyOperation()
		)
	}
}