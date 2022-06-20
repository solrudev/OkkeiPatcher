package ru.solrudev.okkeipatcher.domain.service.operation.factory

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.factory.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.PatchOperation
import javax.inject.Inject

class PatchOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val patchRepositoryFactory: PatchRepositoryFactory,
	private val strategyFactory: GameFileStrategyFactory,
	@ApplicationContext private val applicationContext: Context
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveDataDao.retrieve()
		val patchRepository = patchRepositoryFactory.create()
		val patchUpdates = patchRepository.getPatchUpdates()
		val patchOperation = PatchOperation(
			strategy,
			handleSaveData,
			patchUpdates,
			preferencesRepository.isPatchedDao,
			applicationContext
		)
		patchOperation.checkCanPatch()
		return patchOperation
	}
}