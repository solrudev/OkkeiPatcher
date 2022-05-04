package ru.solrudev.okkeipatcher.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.solrudev.okkeipatcher.domain.service.PackageInstallerFacade
import ru.solrudev.okkeipatcher.domain.service.PackageInstallerFacadeImpl

@InstallIn(SingletonComponent::class)
@Module
interface PackageInstallerBindModule {

	@Binds
	fun bindPackageInstallerFacade(packageInstallerFacadeImpl: PackageInstallerFacadeImpl): PackageInstallerFacade
}