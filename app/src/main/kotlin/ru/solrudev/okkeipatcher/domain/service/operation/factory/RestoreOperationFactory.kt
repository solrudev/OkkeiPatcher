package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.model.RestoreParameters
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.RestoreOperation
import javax.inject.Inject

class RestoreOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategyFactory: GameFileStrategyFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveData.retrieve()
		val parameters = RestoreParameters(handleSaveData)
		return RestoreOperation(
			parameters,
			strategy,
			preferencesRepository.patchVersion,
			preferencesRepository.patchStatus,
			storageChecker
		)
	}
}