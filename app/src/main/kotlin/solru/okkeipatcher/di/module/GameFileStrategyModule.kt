package solru.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.gamefile.strategy.GameFileStrategy
import solru.okkeipatcher.domain.gamefile.strategy.impl.english.DefaultGameFileStrategy
import solru.okkeipatcher.domain.model.Language
import solru.okkeipatcher.util.Preferences
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