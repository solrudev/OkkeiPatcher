package solru.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import solru.okkeipatcher.api.ManifestService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

	@Provides
	@Singleton
	fun provideManifestService(retrofit: Retrofit): ManifestService = retrofit.create(ManifestService::class.java)
}