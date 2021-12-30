package solru.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.model.Language
import solru.okkeipatcher.core.strategy.GameFileStrategy
import solru.okkeipatcher.core.strategy.PatchDataStrategy
import solru.okkeipatcher.core.strategy.impl.english.DefaultGameFileStrategy
import solru.okkeipatcher.core.strategy.impl.english.DefaultPatchDataStrategy
import solru.okkeipatcher.utils.Preferences
import javax.inject.Provider

@InstallIn(SingletonComponent::class)
@Module
object StrategyModule {

	@Provides
	fun providePatchDataStrategy(english: Provider<DefaultPatchDataStrategy>): PatchDataStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english.get()
			else -> throw IllegalStateException("Unknown patch language")
		}

	@Provides
	fun provideGameFileStrategy(english: Provider<DefaultGameFileStrategy>): GameFileStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english.get()
			else -> throw IllegalStateException("Unknown patch language")
		}
}