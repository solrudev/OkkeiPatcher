package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.work.MockPatchWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.MockRestoreWorkRepositoryImpl
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryFlavorModule {

	@Binds
	@Singleton
	fun bindPatchWorkRepository(
		patchWorkRepository: MockPatchWorkRepositoryImpl
	): PatchWorkRepository

	@Binds
	@Singleton
	fun bindRestoreWorkRepository(
		restoreWorkRepository: MockRestoreWorkRepositoryImpl
	): RestoreWorkRepository
}