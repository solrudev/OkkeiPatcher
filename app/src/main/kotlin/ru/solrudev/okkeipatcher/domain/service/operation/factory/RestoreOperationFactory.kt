package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.operation.RestoreOperation
import javax.inject.Inject
import javax.inject.Provider

class RestoreOperationFactory @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategies: Map<Language, @JvmSuppressWildcards Provider<GameFileStrategy>>
) : OperationFactory<Unit> {

	override suspend fun create(): Operation<Unit> {
		val handleSaveData = preferencesRepository.handleSaveDataDao.retrieve()
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		val strategy = strategies.getValue(patchLanguage).get()
		val restoreOperation = RestoreOperation(
			strategy,
			handleSaveData,
			preferencesRepository.isPatchedDao
		)
		restoreOperation.checkCanRestore()
		return restoreOperation
	}
}