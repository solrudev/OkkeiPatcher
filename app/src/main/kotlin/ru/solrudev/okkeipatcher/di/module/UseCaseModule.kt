package ru.solrudev.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.usecase.app.*
import ru.solrudev.okkeipatcher.domain.usecase.app.impl.*
import ru.solrudev.okkeipatcher.domain.usecase.work.*
import ru.solrudev.okkeipatcher.domain.usecase.work.impl.*

@InstallIn(SingletonComponent::class)
@Module(includes = [UseCaseFlavorModule::class])
interface UseCaseModule {

	@Binds
	fun bindEnqueuePatchWorkUseCase(enqueuePatchWorkUseCase: EnqueuePatchWorkUseCaseImpl): EnqueuePatchWorkUseCase

	@Binds
	fun bindEnqueueRestoreWorkUseCase(enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCaseImpl): EnqueueRestoreWorkUseCase

	@Binds
	fun bindGetPatchWorkUseCase(getPatchWorkUseCase: GetPatchWorkUseCaseImpl): GetPatchWorkUseCase

	@Binds
	fun bindGetRestoreWorkUseCase(getRestoreWorkUseCase: GetRestoreWorkUseCaseImpl): GetRestoreWorkUseCase

	@Binds
	fun bindCompleteWorkUseCase(completeWorkUseCase: CompleteWorkUseCaseImpl): CompleteWorkUseCase

	@Binds
	fun bindCancelWorkUseCase(cancelWorkUseCase: CancelWorkUseCaseImpl): CancelWorkUseCase

	@Binds
	fun bindGetWorkStateFlowUseCase(getWorkStateFlowUseCase: GetWorkStateFlowUseCaseImpl): GetWorkStateFlowUseCase

	@Binds
	fun bindGetIsWorkPendingUseCase(getIsWorkPendingUseCase: GetIsWorkPendingUseCaseImpl): GetIsWorkPendingUseCase

	@Binds
	fun bindGetIsAppUpdateAvailableUseCase(getIsAppUpdateAvailableUseCase: GetIsAppUpdateAvailableUseCaseImpl): GetIsAppUpdateAvailableUseCase

	@Binds
	fun bindGetAppUpdateSizeInMbUseCase(getAppUpdateSizeInMbUseCase: GetAppUpdateSizeInMbUseCaseImpl): GetAppUpdateSizeInMbUseCase

	@Binds
	fun bindGetAppUpdateFileUseCase(getAppUpdateFileUseCase: GetAppUpdateFileUseCaseImpl): GetAppUpdateFileUseCase

	@Binds
	fun bindGetAppChangelogUseCase(getAppChangelogUseCase: GetAppChangelogUseCaseImpl): GetAppChangelogUseCase

	@Binds
	fun bindGetIsPatchedUseCase(getIsPatchedUseCase: GetIsPatchedUseCaseImpl): GetIsPatchedUseCase

	@Binds
	fun bindGetPatchLanguageUseCase(getPatchLanguageUseCase: GetPatchLanguageUseCaseImpl): GetPatchLanguageUseCase
}