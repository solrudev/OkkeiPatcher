package ru.solrudev.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english.MockGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.impl.english.MockGetPatchUpdatesUseCase

@InstallIn(SingletonComponent::class)
@Module(includes = [UseCaseBindModule::class])
object UseCaseModule {

	@Provides
	fun provideGetPatchUpdatesUseCase(mock: MockGetPatchUpdatesUseCase): GetPatchUpdatesUseCase = mock

	@Provides
	fun provideGetPatchSizeInMbUseCase(mock: MockGetPatchSizeInMbUseCase): GetPatchSizeInMbUseCase = mock
}