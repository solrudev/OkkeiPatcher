package ru.solrudev.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english.DefaultGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english.DefaultGetPatchUpdatesUseCase

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseFlavorModule {

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun provideGetPatchUpdatesUseCase(
		defaultGetPatchUpdatesUseCase: DefaultGetPatchUpdatesUseCase
	): GetPatchUpdatesUseCase

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun provideGetPatchSizeInMbUseCase(
		defaultGetPatchSizeInMbUseCase: DefaultGetPatchSizeInMbUseCase
	): GetPatchSizeInMbUseCase
}