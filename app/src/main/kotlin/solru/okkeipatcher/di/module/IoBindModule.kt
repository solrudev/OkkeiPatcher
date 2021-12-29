package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.io.services.impl.HttpDownloaderImpl
import solru.okkeipatcher.io.services.impl.StreamCopierImpl

@InstallIn(SingletonComponent::class)
@Module
interface IoBindModule {

	@Binds
	@Reusable
	fun bindHttpDownloader(httpDownloaderImpl: HttpDownloaderImpl): HttpDownloader

	@Binds
	@Reusable
	fun bindStreamCopier(streamCopierImpl: StreamCopierImpl): StreamCopier
}