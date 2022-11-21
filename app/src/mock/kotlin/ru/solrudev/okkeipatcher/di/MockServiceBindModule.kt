package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.data.service.OkkeiPatcherApkProvider
import ru.solrudev.okkeipatcher.data.service.OkkeiPatcherApkProviderImpl

@InstallIn(SingletonComponent::class)
@Module
interface MockServiceBindModule {

	@Binds
	@Reusable
	fun bindOkkeiPatcherApkProvider(okkeiPatcherApkProvider: OkkeiPatcherApkProviderImpl): OkkeiPatcherApkProvider
}