package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.repository.app.OkkeiPatcherRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.PatchWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.repository.work.RestoreWorkRepositoryImpl
import ru.solrudev.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.RestoreWorkRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryFlavorModule {

	@Binds
	@Singleton
	fun bindOkkeiPatcherRepository(okkeiPatcherRepository: OkkeiPatcherRepositoryImpl): OkkeiPatcherRepository

	@Binds
	@Singleton
	fun bindPatchWorkRepository(patchWorkRepository: PatchWorkRepositoryImpl): PatchWorkRepository

	@Binds
	@Singleton
	fun bindRestoreWorkRepository(restoreWorkRepository: RestoreWorkRepositoryImpl): RestoreWorkRepository
}