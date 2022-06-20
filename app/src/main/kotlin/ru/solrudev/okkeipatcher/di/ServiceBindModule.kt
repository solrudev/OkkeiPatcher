package ru.solrudev.okkeipatcher.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.service.ApkSigner
import ru.solrudev.okkeipatcher.domain.service.ApkSignerImpl

@InstallIn(SingletonComponent::class)
@Module
interface ServiceBindModule {

	@Binds
	fun bindApkSigner(apkSignerImpl: ApkSignerImpl): ApkSigner
}