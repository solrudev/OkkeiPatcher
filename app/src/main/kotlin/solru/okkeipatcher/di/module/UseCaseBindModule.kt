package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import solru.okkeipatcher.domain.usecase.app.GetAppChangelogUseCase
import solru.okkeipatcher.domain.usecase.app.GetAppUpdateFileUseCase
import solru.okkeipatcher.domain.usecase.app.GetAppUpdateSizeInMbUseCase
import solru.okkeipatcher.domain.usecase.app.GetIsAppUpdateAvailableUseCase
import solru.okkeipatcher.domain.usecase.app.impl.GetAppChangelogUseCaseImpl
import solru.okkeipatcher.domain.usecase.app.impl.GetAppUpdateFileUseCaseImpl
import solru.okkeipatcher.domain.usecase.app.impl.GetAppUpdateSizeInMbUseCaseImpl
import solru.okkeipatcher.domain.usecase.app.impl.GetIsAppUpdateAvailableUseCaseImpl
import solru.okkeipatcher.domain.usecase.work.*
import solru.okkeipatcher.domain.usecase.work.impl.*

@InstallIn(ViewModelComponent::class)
@Module
interface UseCaseBindModule {

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
}