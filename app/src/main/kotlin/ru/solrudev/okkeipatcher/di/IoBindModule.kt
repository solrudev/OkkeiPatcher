package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.service.HttpDownloader
import ru.solrudev.okkeipatcher.domain.service.HttpDownloaderImpl
import ru.solrudev.okkeipatcher.domain.service.StreamCopier
import ru.solrudev.okkeipatcher.domain.service.StreamCopierImpl

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