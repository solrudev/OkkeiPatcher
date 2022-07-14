package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.service.*
import ru.solrudev.okkeipatcher.domain.service.StorageChecker

@InstallIn(SingletonComponent::class)
@Module
interface ServiceBindModule {

	@Binds
	@Reusable
	fun bindStorageChecker(
		storageChecker: StorageCheckerImpl
	): StorageChecker

	@Binds
	@Reusable
	fun bindApkSigner(
		apkSigner: ApkSignerImpl
	): ApkSigner

	@Binds
	@Reusable
	fun bindFileDownloader(
		fileDownloader: FileDownloaderImpl
	): FileDownloader

	@Binds
	@Reusable
	fun bindStreamCopier(
		streamCopier: StreamCopierImpl
	): StreamCopier
}