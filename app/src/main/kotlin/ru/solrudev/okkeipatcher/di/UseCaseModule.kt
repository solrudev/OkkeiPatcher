package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.solrudev.okkeipatcher.domain.usecase.app.*
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchVersionFlowUseCase
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchVersionFlowUseCaseImpl
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
	fun bindGetPendingWorkFlowUseCase(
		getPendingWorkFlowUseCase: GetPendingWorkFlowUseCaseImpl
	): GetPendingWorkFlowUseCase

	@Binds
	fun bindGetIsWorkPendingFlowUseCase(
		getIsWorkPendingFlowUseCase: GetIsWorkPendingFlowUseCaseImpl
	): GetIsWorkPendingFlowUseCase

	@Binds
	fun bindGetPatchStatusUseCase(
		getPatchStatusFlowUseCase: GetPatchStatusFlowUseCaseImpl
	): GetPatchStatusFlowUseCase

	@Binds
	fun bindGetPatchVersionFlowUseCase(
		getPatchVersionFlowUseCase: GetPatchVersionFlowUseCaseImpl
	): GetPatchVersionFlowUseCase

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

	@Binds
	fun bindClearDataUseCase(
		clearDataUseCase: ClearDataUseCaseImpl
	): ClearDataUseCase

	@Binds
	fun bindGetLicensesUseCase(
		getLicensesUseCase: GetLicensesUseCaseImpl
	): GetLicensesUseCase

	@Binds
	fun bindGetUpdateDataUseCase(
		getUpdateDataUseCase: GetUpdateDataUseCaseImpl
	): GetUpdateDataUseCase

	@Binds
	fun bindInstallUpdateUseCase(
		installUpdateUseCase: InstallUpdateUseCaseImpl
	): InstallUpdateUseCase

	@Binds
	fun bindEnqueueUpdateDownloadWorkUseCase(
		enqueueUpdateDownloadWorkUseCase: EnqueueUpdateDownloadWorkUseCaseImpl
	): EnqueueUpdateDownloadWorkUseCase

	@Binds
	fun bindGetPendingUpdateDownloadWorkFlowUseCase(
		getPendingUpdateDownloadWorkFlowUseCase: GetPendingUpdateDownloadWorkFlowUseCaseImpl
	): GetPendingUpdateDownloadWorkFlowUseCase
}