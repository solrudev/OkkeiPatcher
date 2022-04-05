package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.data.repository.app.OkkeiPatcherRepositoryImpl
import solru.okkeipatcher.data.repository.patch.DefaultPatchRepositoryImpl
import solru.okkeipatcher.data.repository.work.PatchWorkRepositoryImpl
import solru.okkeipatcher.data.repository.work.RestoreWorkRepositoryImpl
import solru.okkeipatcher.data.repository.work.WorkRepositoryImpl
import solru.okkeipatcher.domain.repository.app.OkkeiPatcherRepository
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.domain.repository.work.PatchWorkRepository
import solru.okkeipatcher.domain.repository.work.RestoreWorkRepository
import solru.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryBindModule {

	@Binds
	@Singleton
	fun bindOkkeiPatcherRepository(okkeiPatcherRepository: OkkeiPatcherRepositoryImpl): OkkeiPatcherRepository

	@Binds
	@Singleton
	fun bindDefaultPatchRepository(defaultPatchRepository: DefaultPatchRepositoryImpl): DefaultPatchRepository

	@Binds
	@Singleton
	fun bindWorkRepository(workRepository: WorkRepositoryImpl): WorkRepository

	@Binds
	@Singleton
	fun bindPatchWorkRepository(patchWorkRepository: PatchWorkRepositoryImpl): PatchWorkRepository

	@Binds
	@Singleton
	fun bindRestoreWorkRepository(restoreWorkRepository: RestoreWorkRepositoryImpl): RestoreWorkRepository
}