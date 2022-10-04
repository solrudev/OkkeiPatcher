package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.model.PatchParameters
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.PatchOperation
import javax.inject.Inject

class PatchOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val patchRepositoryFactory: PatchRepositoryFactory,
	private val strategyFactory: GameFileStrategyFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveData.retrieve()
		val patchRepository = patchRepositoryFactory.create()
		val patchUpdates = patchRepository.getPatchUpdates()
		val patchVersion = patchRepository.getDisplayVersion()
		val parameters = PatchParameters(handleSaveData, patchUpdates, patchVersion)
		return PatchOperation(
			parameters,
			strategy,
			preferencesRepository.patchVersion,
			preferencesRepository.patchStatus,
			storageChecker
		)
	}
}