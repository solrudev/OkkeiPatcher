package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.MockGetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.MockGetPatchUpdatesUseCase

@InstallIn(ViewModelComponent::class)
@Module
interface UseCaseFlavorModule {

	@Binds
	fun bindGetPatchUpdatesUseCase(getPatchUpdatesUseCase: MockGetPatchUpdatesUseCase): GetPatchUpdatesUseCase

	@Binds
	fun bindGetPatchSizeInMbUseCase(getPatchSizeInMbUseCase: MockGetPatchSizeInMbUseCase): GetPatchSizeInMbUseCase
}