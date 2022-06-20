package ru.solrudev.okkeipatcher.domain.service.operation.factory

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.factory.GameFileStrategyFactory
import ru.solrudev.okkeipatcher.domain.service.operation.RestoreOperation
import javax.inject.Inject

class RestoreOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategyFactory: GameFileStrategyFactory,
	@ApplicationContext private val applicationContext: Context
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val strategy = strategyFactory.create()
		val handleSaveData = preferencesRepository.handleSaveData.retrieve()
		val restoreOperation = RestoreOperation(
			strategy,
			handleSaveData,
			preferencesRepository.isPatched,
			applicationContext
		)
		restoreOperation.checkCanRestore()
		return restoreOperation
	}
}