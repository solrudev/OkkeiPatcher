package solru.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.model.Language
import solru.okkeipatcher.core.strategy.GameFileStrategy
import solru.okkeipatcher.core.strategy.PatchDataStrategy
import solru.okkeipatcher.core.strategy.impl.english.EnglishGameFileStrategy
import solru.okkeipatcher.core.strategy.impl.english.EnglishPatchDataStrategy
import solru.okkeipatcher.utils.Preferences

@InstallIn(SingletonComponent::class)
@Module
object StrategyModule {

	@Provides
	fun providePatchDataStrategy(english: EnglishPatchDataStrategy): PatchDataStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english
			else -> throw IllegalStateException("Unknown patch language")
		}

	@Provides
	fun provideGameFileStrategy(english: EnglishGameFileStrategy): GameFileStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english
			else -> throw IllegalStateException("Unknown patch language")
		}
}