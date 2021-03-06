package ru.solrudev.okkeipatcher.domain.service.gamefile.strategy

import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject
import javax.inject.Provider

interface GameFileStrategyFactory : SuspendFactory<GameFileStrategy>

class GameFileStrategyFactoryImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategies: Map<Language, @JvmSuppressWildcards Provider<GameFileStrategy>>
) : GameFileStrategyFactory {

	override suspend fun create(): GameFileStrategy {
		val patchLanguage = preferencesRepository.patchLanguage.retrieve()
		return strategies.getValue(patchLanguage).get()
	}
}