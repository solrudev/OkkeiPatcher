package solru.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module(includes = [IoBindModule::class])
object IoModule {

	@Provides
	@Reusable
	fun provideIoDispatcher() = Dispatchers.IO
}