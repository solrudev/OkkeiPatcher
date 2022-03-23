package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.domain.usecase.*
import solru.okkeipatcher.domain.usecase.impl.*

@InstallIn(SingletonComponent::class)
@Module
interface UseCaseBindModule {

	@Binds
	@Reusable
	fun bindEnqueuePatchWorkUseCase(enqueuePatchWorkUseCase: EnqueuePatchWorkUseCaseImpl): EnqueuePatchWorkUseCase

	@Binds
	@Reusable
	fun bindEnqueueRestoreWorkUseCase(enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCaseImpl): EnqueueRestoreWorkUseCase

	@Binds
	@Reusable
	fun bindGetPatchWorkUseCase(getPatchWorkUseCase: GetPatchWorkUseCaseImpl): GetPatchWorkUseCase

	@Binds
	@Reusable
	fun bindGetRestoreWorkUseCase(getRestoreWorkUseCase: GetRestoreWorkUseCaseImpl): GetRestoreWorkUseCase

	@Binds
	@Reusable
	fun bindCompleteWorkUseCase(completeWorkUseCase: CompleteWorkUseCaseImpl): CompleteWorkUseCase

	@Binds
	@Reusable
	fun bindGetIsWorkPendingUseCase(getIsWorkPendingUseCase: GetIsWorkPendingUseCaseImpl): GetIsWorkPendingUseCase

	@Binds
	@Reusable
	fun bindIsAppUpdateAvailableUseCase(isAppUpdateAvailableUseCase: IsAppUpdateAvailableUseCaseImpl): IsAppUpdateAvailableUseCase

	@Binds
	@Reusable
	fun bindGetAppUpdateSizeInMbUseCase(getAppUpdateSizeInMbUseCase: GetAppUpdateSizeInMbUseCaseImpl): GetAppUpdateSizeInMbUseCase

	@Binds
	@Reusable
	fun bindGetAppUpdateFileUseCase(getAppUpdateFileUseCase: GetAppUpdateFileUseCaseImpl): GetAppUpdateFileUseCase

	@Binds
	@Reusable
	fun bindGetAppChangelogUseCase(getAppChangelogUseCase: GetAppChangelogUseCaseImpl): GetAppChangelogUseCase

	@Binds
	@Reusable
	fun bindClearNotificationsUseCase(clearNotificationsUseCase: ClearNotificationsUseCaseImpl): ClearNotificationsUseCase
}