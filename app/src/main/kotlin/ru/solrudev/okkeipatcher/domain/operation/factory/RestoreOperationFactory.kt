package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.game.GameFactory
import ru.solrudev.okkeipatcher.domain.model.RestoreParameters
import ru.solrudev.okkeipatcher.domain.operation.RestoreOperation
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import javax.inject.Inject

class RestoreOperationFactory @Inject constructor(
	private val patchStateRepository: PatchStateRepository,
	private val gameFactory: GameFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val game = gameFactory.create()
		val handleSaveData = patchStateRepository.handleSaveData.retrieve()
		val parameters = RestoreParameters(handleSaveData)
		return RestoreOperation(
			parameters,
			game,
			patchStateRepository.patchVersion,
			patchStateRepository.patchStatus,
			storageChecker
		)
	}
}