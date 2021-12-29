package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import solru.okkeipatcher.repository.impl.OkkeiPatcherRepositoryImpl
import solru.okkeipatcher.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.repository.patch.impl.DefaultPatchRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module()
interface RepositoryBindModule {

	@Binds
	@Singleton
	fun bindOkkeiPatcherRepository(okkeiPatcherRepository: OkkeiPatcherRepositoryImpl): OkkeiPatcherRepository

	@Binds
	@Singleton
	fun bindDefaultPatchRepository(defaultPatchRepository: DefaultPatchRepositoryImpl): DefaultPatchRepository
}