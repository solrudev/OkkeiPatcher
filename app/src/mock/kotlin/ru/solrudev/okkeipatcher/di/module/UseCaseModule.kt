package ru.solrudev.okkeipatcher.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.MockGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.MockGetPatchUpdatesUseCase

@InstallIn(SingletonComponent::class)
@Module(includes = [UseCaseBindModule::class])
object UseCaseModule {

	@Provides
	fun provideGetPatchUpdatesUseCase(mock: MockGetPatchUpdatesUseCase): GetPatchUpdatesUseCase = mock

	@Provides
	fun provideGetPatchSizeInMbUseCase(mock: MockGetPatchSizeInMbUseCase): GetPatchSizeInMbUseCase = mock
}