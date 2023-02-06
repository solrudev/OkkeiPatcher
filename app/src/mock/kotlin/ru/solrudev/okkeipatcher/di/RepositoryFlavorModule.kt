@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.app.repository.OkkeiPatcherRepository
import ru.solrudev.okkeipatcher.app.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.data.repository.app.MockOkkeiPatcherRepository
import ru.solrudev.okkeipatcher.data.repository.work.MockPatchWorkRepository
import ru.solrudev.okkeipatcher.data.repository.work.MockRestoreWorkRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryFlavorModule {

	@Binds
	@Singleton
	fun bindOkkeiPatcherRepository(okkeiPatcherRepository: MockOkkeiPatcherRepository): OkkeiPatcherRepository

	@Binds
	@Singleton
	fun bindPatchWorkRepository(patchWorkRepository: MockPatchWorkRepository): PatchWorkRepository

	@Binds
	@Singleton
	fun bindRestoreWorkRepository(restoreWorkRepository: MockRestoreWorkRepository): RestoreWorkRepository
}