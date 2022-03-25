package solru.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.io.service.StreamCopier
import solru.okkeipatcher.io.service.impl.HttpDownloaderImpl
import solru.okkeipatcher.io.service.impl.StreamCopierImpl

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