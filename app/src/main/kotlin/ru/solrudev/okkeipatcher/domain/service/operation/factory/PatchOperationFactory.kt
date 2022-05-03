package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.operation.PatchOperation
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import javax.inject.Inject
import javax.inject.Provider

class PatchOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val getPatchUpdatesUseCases: Map<Language, @JvmSuppressWildcards Provider<GetPatchUpdatesUseCase>>,
	private val strategies: Map<Language, @JvmSuppressWildcards Provider<GameFileStrategy>>
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val handleSaveData = preferencesRepository.handleSaveDataDao.retrieve()
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		val getPatchUpdatesUseCase = getPatchUpdatesUseCases.getValue(patchLanguage).get()
		val patchUpdates = getPatchUpdatesUseCase()
		val strategy = strategies.getValue(patchLanguage).get()
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