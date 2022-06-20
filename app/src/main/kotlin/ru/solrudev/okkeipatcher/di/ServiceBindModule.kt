package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.service.ApkSigner
import ru.solrudev.okkeipatcher.domain.service.ApkSignerImpl
import ru.solrudev.okkeipatcher.domain.service.StorageChecker
import ru.solrudev.okkeipatcher.domain.service.StorageCheckerImpl

@InstallIn(SingletonComponent::class)
@Module
interface ServiceBindModule {

	@Binds
	fun bindApkSigner(apkSignerImpl: ApkSignerImpl): ApkSigner

	@Binds
	fun bindStorageChecker(storageCheckerImpl: StorageCheckerImpl): StorageChecker
}