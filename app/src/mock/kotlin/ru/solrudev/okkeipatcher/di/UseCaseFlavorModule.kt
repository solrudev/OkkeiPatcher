package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.MockGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.MockGetPatchUpdatesUseCase

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseFlavorModule {

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindGetPatchUpdatesUseCase(
		mockGetPatchUpdatesUseCase: MockGetPatchUpdatesUseCase
	): GetPatchUpdatesUseCase

	@Binds
	@IntoMap
	@LanguageKey(Language.English)
	fun bindGetPatchSizeInMbUseCase(
		mockGetPatchSizeInMbUseCase: MockGetPatchSizeInMbUseCase
	): GetPatchSizeInMbUseCase
}