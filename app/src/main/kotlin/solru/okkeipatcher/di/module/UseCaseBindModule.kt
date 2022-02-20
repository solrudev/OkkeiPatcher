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
	fun bindCancelWorkUseCase(cancelWorkUseCase: CancelWorkUseCaseImpl): CancelWorkUseCase

	@Binds
	@Reusable
	fun bindGetWorkStateFlowByIdUseCase(getWorkStateFlowByIdUseCase: GetWorkStateFlowByIdUseCaseImpl): GetWorkStateFlowByIdUseCase
}