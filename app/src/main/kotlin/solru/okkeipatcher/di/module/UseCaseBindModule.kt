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
	fun bindStartPatchWorkUseCase(startPatchWorkUseCase: StartPatchWorkUseCaseImpl): StartPatchWorkUseCase

	@Binds
	@Reusable
	fun bindStartRestoreWorkUseCase(startRestoreWorkUseCase: StartRestoreWorkUseCaseImpl): StartRestoreWorkUseCase

	@Binds
	@Reusable
	fun bindGetPatchWorkUuidUseCase(getPatchWorkUuidUseCase: GetPatchWorkUuidUseCaseImpl): GetPatchWorkUuidUseCase

	@Binds
	@Reusable
	fun bindGetRestoreWorkUuidUseCase(getRestoreWorkUuidUseCase: GetRestoreWorkUuidUseCaseImpl): GetRestoreWorkUuidUseCase

	@Binds
	@Reusable
	fun bindCancelWorkByIdUseCase(cancelWorkByIdUseCase: CancelWorkByIdUseCaseImpl): CancelWorkByIdUseCase

	@Binds
	@Reusable
	fun bindGetWorkStateFlowByIdUseCase(getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCaseImpl): GetWorkStateFlowByIdUseCase

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