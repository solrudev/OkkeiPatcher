package solru.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.io.services.base.*
import solru.okkeipatcher.io.services.impl.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface IoBindModule {

	@Binds
	@Singleton
	fun bindIoService(ioServiceImpl: IoServiceImpl): IoService

	@Binds
	@Singleton
	fun bindHttpDownloader(httpDownloaderImpl: HttpDownloaderImpl): HttpDownloader

	@Binds
	@Singleton
	fun bindHashGenerator(sha256Generator: Sha256Generator): HashGenerator

	@Binds
	@Singleton
	fun bindStreamCopier(streamCopierImpl: StreamCopierImpl): StreamCopier

	@Binds
	@Singleton
	fun bindTextReader(textReaderImpl: TextReaderImpl): TextReader

	@Binds
	@Singleton
	fun bindTextWriter(textWriterImpl: TextWriterImpl): TextWriter
}