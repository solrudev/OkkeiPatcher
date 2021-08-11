package solru.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import solru.okkeipatcher.io.services.base.*
import solru.okkeipatcher.io.services.impl.*

@InstallIn(SingletonComponent::class)
@Module
interface IoBindModule {

	@Binds
	@Reusable
	fun bindIoService(ioServiceImpl: IoServiceImpl): IoService

	@Binds
	@Reusable
	fun bindHttpDownloader(httpDownloaderImpl: HttpDownloaderImpl): HttpDownloader

	@Binds
	@Reusable
	fun bindHashGenerator(sha256Generator: Sha256Generator): HashGenerator

	@Binds
	@Reusable
	fun bindStreamCopier(streamCopierImpl: StreamCopierImpl): StreamCopier

	@Binds
	@Reusable
	fun bindTextReader(textReaderImpl: TextReaderImpl): TextReader

	@Binds
	@Reusable
	fun bindTextWriter(textWriterImpl: TextWriterImpl): TextWriter
}