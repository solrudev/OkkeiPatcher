package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.game.GameFactory
import ru.solrudev.okkeipatcher.domain.model.RestoreParameters
import ru.solrudev.okkeipatcher.domain.operation.RestoreOperation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import javax.inject.Inject

class RestoreOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val gameFactory: GameFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val game = gameFactory.create()
		val handleSaveData = preferencesRepository.handleSaveData.retrieve()
		val parameters = RestoreParameters(handleSaveData)
		return RestoreOperation(
			parameters,
			game,
			preferencesRepository.patchVersion,
			preferencesRepository.patchStatus,
			storageChecker
		)
	}
}