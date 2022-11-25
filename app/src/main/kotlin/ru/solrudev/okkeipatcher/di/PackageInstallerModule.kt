@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.solrudev.simpleinstaller.PackageInstaller
import io.github.solrudev.simpleinstaller.PackageUninstaller

@InstallIn(SingletonComponent::class)
@Module
object PackageInstallerModule {

	@Provides
	fun providePackageInstaller(): PackageInstaller = PackageInstaller

	@Provides
	fun providePackageUninstaller(): PackageUninstaller = PackageUninstaller
}