package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.OkkeiStorage
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.operation.AggregateOperation
import ru.solrudev.okkeipatcher.domain.operation.EmptyOperation
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy

class PatchOperation(
	private val strategy: GameFileStrategy,
	private val handleSaveData: Boolean,
	private val patchUpdates: PatchUpdates,

	// TODO: accept interface which gives access only to the patch state instead of the whole repository
	//  Related to segregation of PreferencesRepository
	private val preferencesRepository: PreferencesRepository
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
		val isPatched = preferencesRepository.getIsPatched()
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
		override suspend fun doAfter() = preferencesRepository.setIsPatched(true)
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