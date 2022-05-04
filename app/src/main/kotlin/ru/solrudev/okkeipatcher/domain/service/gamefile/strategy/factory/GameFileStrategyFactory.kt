package ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.factory

import ru.solrudev.okkeipatcher.domain.factory.Factory
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import javax.inject.Inject
import javax.inject.Provider

interface GameFileStrategyFactory : Factory<GameFileStrategy>

class GameFileStrategyFactoryImpl @Inject constructor(
	private val preferencesRepository: PreferencesRepository,
	private val strategies: Map<Language, @JvmSuppressWildcards Provider<GameFileStrategy>>
) : GameFileStrategyFactory {

	override suspend fun create(): GameFileStrategy {
		val patchLanguage = preferencesRepository.patchLanguageDao.retrieve()
		return strategies.getValue(patchLanguage).get()
	}
}