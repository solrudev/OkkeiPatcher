package solru.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.repository.ManifestRepository
import solru.okkeipatcher.repository.OkkeiPatcherRepository
import solru.okkeipatcher.repository.impl.ManifestRepositoryImpl
import solru.okkeipatcher.repository.impl.OkkeiPatcherRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module()
interface RepositoryBindModule {

	@Binds
	@Singleton
	fun bindManifestRepository(manifestRepository: ManifestRepositoryImpl): ManifestRepository

	@Binds
	@Singleton
	fun bindOkkeiPatcherRepository(okkeiPatcherRepository: OkkeiPatcherRepositoryImpl): OkkeiPatcherRepository
}