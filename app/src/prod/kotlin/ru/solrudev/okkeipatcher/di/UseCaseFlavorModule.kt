package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCaseImpl
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCaseImpl

@InstallIn(ViewModelComponent::class)
@Module
interface UseCaseFlavorModule {

	@Binds
	fun bindGetPatchUpdatesUseCase(
		getPatchUpdatesUseCase: GetPatchUpdatesUseCaseImpl
	): GetPatchUpdatesUseCase

	@Binds
	fun bindGetPatchSizeInMbUseCase(
		getPatchSizeInMbUseCase: GetPatchSizeInMbUseCaseImpl
	): GetPatchSizeInMbUseCase
}