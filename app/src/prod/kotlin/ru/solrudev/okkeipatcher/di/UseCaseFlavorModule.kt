package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.english.DefaultGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.english.DefaultGetPatchUpdatesUseCase

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseFlavorModule {

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindGetPatchUpdatesUseCase(
		defaultGetPatchUpdatesUseCase: DefaultGetPatchUpdatesUseCase
	): GetPatchUpdatesUseCase

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindGetPatchSizeInMbUseCase(
		defaultGetPatchSizeInMbUseCase: DefaultGetPatchSizeInMbUseCase
	): GetPatchSizeInMbUseCase
}