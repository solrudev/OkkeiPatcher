package solru.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.strategy.GameFileStrategy
import solru.okkeipatcher.core.strategy.PatchInfoStrategy
import solru.okkeipatcher.core.strategy.impl.english.GameFileStrategyEnglish
import solru.okkeipatcher.core.strategy.impl.english.PatchInfoStrategyEnglish
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.utils.Preferences

@InstallIn(SingletonComponent::class)
@Module
object StrategiesModule {

	@Provides
	fun providePatchInfoStrategy(): PatchInfoStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> PatchInfoStrategyEnglish()
			else -> throw IllegalStateException("Unknown patch language")
		}

	@Provides
	fun provideGameFileStrategy(english: GameFileStrategyEnglish): GameFileStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english
			else -> throw IllegalStateException("Unknown patch language")
		}
}