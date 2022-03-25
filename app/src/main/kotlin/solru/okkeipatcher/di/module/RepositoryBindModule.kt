package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.data.repository.OkkeiPatcherRepositoryImpl
import solru.okkeipatcher.data.repository.WorkRepositoryImpl
import solru.okkeipatcher.data.repository.patch.DefaultPatchRepositoryImpl
import solru.okkeipatcher.domain.repository.OkkeiPatcherRepository
import solru.okkeipatcher.domain.repository.WorkRepository
import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
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
}