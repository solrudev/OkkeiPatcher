package ru.solrudev.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.AppKey
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.impl.english.DefaultGameFileStrategy
import ru.solrudev.okkeipatcher.util.Preferences
import javax.inject.Provider

@InstallIn(SingletonComponent::class)
@Module
object GameFileStrategyModule {

	@Provides
	fun provideGameFileStrategy(english: Provider<DefaultGameFileStrategy>): GameFileStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english.get()
			else -> throw IllegalStateException("Unknown patch language")
		}
}