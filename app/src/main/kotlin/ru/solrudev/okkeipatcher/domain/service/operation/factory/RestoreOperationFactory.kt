package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.factory.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.RestoreOperation
import javax.inject.Inject

class RestoreOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategyFactory: GameFileStrategyFactory
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveDataDao.retrieve()
		val restoreOperation = RestoreOperation(
			strategy,
			handleSaveData,
			preferencesRepository.isPatchedDao
		)
		restoreOperation.checkCanRestore()
		return restoreOperation
	}
}