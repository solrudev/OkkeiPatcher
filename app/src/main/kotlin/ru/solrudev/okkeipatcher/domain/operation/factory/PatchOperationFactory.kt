package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.game.GameFactory
import ru.solrudev.okkeipatcher.domain.model.PatchParameters
import ru.solrudev.okkeipatcher.domain.operation.PatchOperation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import javax.inject.Inject

class PatchOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val patchRepositoryFactory: PatchRepositoryFactory,
	private val gameFactory: GameFactory,
	private val storageChecker: StorageChecker
) : OperationFactory<Result> {

	override suspend fun create(): Operation<Result> {
		val game = gameFactory.create()
		val handleSaveData = preferencesRepository.handleSaveData.retrieve()
		val patchRepository = patchRepositoryFactory.create()
		val patchUpdates = patchRepository.getPatchUpdates()
		val patchVersion = patchRepository.getDisplayVersion()
		val parameters = PatchParameters(handleSaveData, patchUpdates, patchVersion)
		return PatchOperation(
			parameters,
			game,
			preferencesRepository.patchVersion,
			preferencesRepository.patchStatus,
			storageChecker
		)
	}
}