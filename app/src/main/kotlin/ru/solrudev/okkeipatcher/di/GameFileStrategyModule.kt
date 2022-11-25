@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.DefaultGameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy

@InstallIn(SingletonComponent::class)
@Module
interface GameFileStrategyModule {

	@[Binds IntoMap]
	@LanguageKey(Language.English)
	fun bindDefaultGameFileStrategy(defaultGameFileStrategy: DefaultGameFileStrategy): GameFileStrategy
}