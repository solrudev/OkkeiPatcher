package ru.solrudev.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.AppKey
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english.DefaultGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english.DefaultGetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.util.Preferences
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