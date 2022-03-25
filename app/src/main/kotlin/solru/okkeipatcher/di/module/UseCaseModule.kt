package solru.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.model.Language
import solru.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import solru.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import solru.okkeipatcher.domain.usecase.patch.impl.english.DefaultGetPatchSizeInMbUseCase
import solru.okkeipatcher.domain.usecase.patch.impl.english.DefaultGetPatchUpdatesUseCase
import solru.okkeipatcher.util.Preferences
import javax.inject.Provider

@InstallIn(SingletonComponent::class)
@Module(includes = [UseCaseBindModule::class])
object UseCaseModule {

	@Provides
	fun provideGetPatchUpdatesUseCase(english: Provider<DefaultGetPatchUpdatesUseCase>): GetPatchUpdatesUseCase =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english.get()
			else -> throw IllegalStateException("Unknown patch language")
		}

	@Provides
	fun provideGetPatchSizeInMbUseCase(english: Provider<DefaultGetPatchSizeInMbUseCase>): GetPatchSizeInMbUseCase =
		when (Preferences.get(AppKey.patch_language.name, Language.English.name)) {
			Language.English.name -> english.get()
			else -> throw IllegalStateException("Unknown patch language")
		}
}