package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.solrudev.okkeipatcher.domain.usecase.app.*
import ru.solrudev.okkeipatcher.domain.usecase.work.*

@InstallIn(ViewModelComponent::class)
@Module(includes = [UseCaseFlavorModule::class])
interface UseCaseModule {

	@Binds
	fun bindEnqueuePatchWorkUseCase(
		enqueuePatchWorkUseCase: EnqueuePatchWorkUseCaseImpl
	): EnqueuePatchWorkUseCase

	@Binds
	fun bindEnqueueRestoreWorkUseCase(
		enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCaseImpl
	): EnqueueRestoreWorkUseCase

	@Binds
	fun bindGetPatchWorkUseCase(
		getPatchWorkUseCase: GetPatchWorkUseCaseImpl
	): GetPatchWorkUseCase

	@Binds
	fun bindGetRestoreWorkUseCase(
		getRestoreWorkUseCase: GetRestoreWorkUseCaseImpl
	): GetRestoreWorkUseCase

	@Binds
	fun bindCompleteWorkUseCase(
		completeWorkUseCase: CompleteWorkUseCaseImpl
	): CompleteWorkUseCase

	@Binds
	fun bindCancelWorkUseCase(
		cancelWorkUseCase: CancelWorkUseCaseImpl
	): CancelWorkUseCase

	@Binds
	fun bindGetWorkStateFlowUseCase(
		getWorkStateFlowUseCase: GetWorkStateFlowUseCaseImpl
	): GetWorkStateFlowUseCase

	@Binds
	fun bindGetIsWorkPendingUseCase(
		getIsWorkPendingUseCase: GetIsWorkPendingUseCaseImpl
	): GetIsWorkPendingUseCase

	@Binds
	fun bindGetIsAppUpdateAvailableUseCase(
		getIsAppUpdateAvailableUseCase: GetIsAppUpdateAvailableUseCaseImpl
	): GetIsAppUpdateAvailableUseCase

	@Binds
	fun bindGetAppUpdateSizeInMbUseCase(
		getAppUpdateSizeInMbUseCase: GetAppUpdateSizeInMbUseCaseImpl
	): GetAppUpdateSizeInMbUseCase

	@Binds
	fun bindGetAppUpdateFileUseCase(
		getAppUpdateFileUseCase: GetAppUpdateFileUseCaseImpl
	): GetAppUpdateFileUseCase

	@Binds
	fun bindGetAppChangelogUseCase(
		getAppChangelogUseCase: GetAppChangelogUseCaseImpl
	): GetAppChangelogUseCase

	@Binds
	fun bindGetIsPatchedUseCase(
		getIsPatchedFlowUseCase: GetPatchStatusFlowUseCaseImpl
	): GetPatchStatusFlowUseCase

	@Binds
	fun bindGetIsSaveDataAccessGrantedUseCase(
		getIsSaveDataAccessGrantedUseCase: GetIsSaveDataAccessGrantedUseCaseImpl
	): GetIsSaveDataAccessGrantedUseCase

	@Binds
	fun bindPersistHandleSaveDataUseCase(
		persistHandleSaveDataUseCase: PersistHandleSaveDataUseCaseImpl
	): PersistHandleSaveDataUseCase

	@Binds
	fun bindGetHandleSaveDataFlowUseCase(
		getHandleSaveDataFlowUseCase: GetHandleSaveDataFlowUseCaseImpl
	): GetHandleSaveDataFlowUseCase

	@Binds
	fun bindCheckSaveDataAccessUseCase(
		checkSaveDataAccessUseCase: CheckSaveDataAccessUseCaseImpl
	): CheckSaveDataAccessUseCase

	@Binds
	fun bindGetRequiredPermissionsUseCase(
		getRequiredPermissionsUseCase: GetRequiredPermissionsUseCaseImpl
	): GetRequiredPermissionsUseCase
}