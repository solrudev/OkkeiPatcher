package solru.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.base.GameFileStrategy
import solru.okkeipatcher.core.base.ManifestStrategy
import solru.okkeipatcher.core.impl.english.GameFileStrategyEnglish
import solru.okkeipatcher.core.impl.english.ManifestStrategyEnglish
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.utils.Preferences

@InstallIn(SingletonComponent::class)
@Module
object StrategiesModule {

	@Provides
	fun provideManifestStrategy(): ManifestStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> ManifestStrategyEnglish()
			else -> throw IllegalStateException("Unknown patch language")
		}

	@Provides
	fun provideGameFileStrategy(english: GameFileStrategyEnglish): GameFileStrategy =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english
			else -> throw IllegalStateException("Unknown patch language")
		}
}