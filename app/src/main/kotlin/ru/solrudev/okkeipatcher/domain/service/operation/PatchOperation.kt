package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.*
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.emptyOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.core.persistence.Persistable
import ru.solrudev.okkeipatcher.domain.model.PatchParameters
import ru.solrudev.okkeipatcher.domain.model.exception.wrapDomainExceptions
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.PatchStrategy

class PatchOperation(
	private val parameters: PatchParameters,
	private val strategy: PatchStrategy,
	private val patchVersion: Persistable<String>,
	private val patchStatus: Dao<Boolean>,
	private val storageChecker: StorageChecker
) : Operation<Result> {

	private val operation = if (parameters.patchUpdates.available) update() else patch()
	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun canInvoke(): Result = with(strategy) {
		val isPatched = patchStatus.retrieve()
		if (isPatched && !parameters.patchUpdates.available) {
			return Result.Failure(LocalizedString.resource(R.string.error_patched))
		}
		apk.canPatch().onFailure { return it }
		obb.canPatch().onFailure { return it }
		if (!storageChecker.isEnoughSpace()) {
			return Result.Failure(LocalizedString.resource(R.string.error_no_free_space))
		}
		return Result.Success
	}

	override suspend fun invoke() = wrapDomainExceptions {
		strategy.use {
			operation()
		}
	}

	private fun patch() = with(strategy) {
		aggregateOperation(
			obb.backup(),
			apk.backup(),
			if (parameters.handleSaveData) saveData.backup() else emptyOperation(),
			apk.patch(),
			if (parameters.handleSaveData) saveData.restore() else emptyOperation(),
			obb.patch(),
			operation {
				patchVersion.persist(parameters.patchVersion)
				patchStatus.persist(true)
			}
		)
	}

	private fun update() = with(strategy) {
		aggregateOperation(
			if (parameters.patchUpdates.apkUpdatesAvailable) apk.update() else emptyOperation(),
			if (parameters.patchUpdates.obbUpdatesAvailable) obb.update() else emptyOperation()
		)
	}
}