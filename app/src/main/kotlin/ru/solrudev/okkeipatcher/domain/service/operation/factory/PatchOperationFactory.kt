package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.factory.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.PatchOperation
import ru.solrudev.okkeipatcher.domain.usecase.patch.factory.GetPatchUpdatesUseCaseFactory
import javax.inject.Inject

class PatchOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val getPatchUpdatesUseCaseFactory: GetPatchUpdatesUseCaseFactory,
	private val strategyFactory: GameFileStrategyFactory
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveDataDao.retrieve()
		val getPatchUpdatesUseCase = getPatchUpdatesUseCaseFactory.create()
		val patchUpdates = getPatchUpdatesUseCase()
		val patchOperation = PatchOperation(
			strategy,
			handleSaveData,
			patchUpdates,
			preferencesRepository.isPatchedDao
		)
		patchOperation.checkCanPatch()
		return patchOperation
	}
}