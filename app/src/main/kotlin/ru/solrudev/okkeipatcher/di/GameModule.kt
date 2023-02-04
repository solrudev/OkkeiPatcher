@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.game.DefaultGame
import ru.solrudev.okkeipatcher.domain.game.Game
import ru.solrudev.okkeipatcher.domain.model.Language

@InstallIn(SingletonComponent::class)
@Module
interface GameModule {

	@[Binds IntoMap]
	@LanguageKey(Language.English)
	fun bindDefaultGame(defaultGame: DefaultGame): Game
}