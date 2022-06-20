package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.factory.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.RestoreOperation
import javax.inject.Inject

class RestoreOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategyFactory: GameFileStrategyFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveData.retrieve()
		val restoreOperation = RestoreOperation(
			strategy,
			handleSaveData,
			preferencesRepository.patchStatus,
			storageChecker
		)
		restoreOperation.checkCanRestore()
		return restoreOperation
	}
}